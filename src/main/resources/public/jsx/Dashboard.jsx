import React from 'react'
import PropTypes from 'prop-types'
import renderIf from 'render-if'
import { translate } from 'react-i18next'

import RowDataset from './RowDataset'
import { Alert } from './Form'
import { H2 } from './Headings'

class Dashboard extends React.Component {
    state = {
        logs: [],
        filterKey: 'all',
        filterValue: 'dashboard.sort.all',
        success: true,
        message: ''
    }
    
    constructor(context){
        super(context)
        this.onChangeFilter = this.onChangeFilter.bind(this)
        this.fetchMapping = this.fetchMapping.bind(this)
        this.closeNotif = this.closeNotif.bind(this)
        this.onChangeNotif = this.onChangeNotif.bind(this)
    }

    componentDidMount() {
        this.fetchMapping()
    }
    fetchMapping() {
        fetch('/api/dc-model-mapping/logs', { credentials: 'same-origin'})
            .then(reponse => reponse.json())
            .then(json => this.setState({ logs: json }))
    }
    onChangeFilter(eventKey) {
        this.setState({ filterKey: eventKey, filterValue: 'dashboard.sort.' + eventKey })
    }
    closeNotif(){
        this.setState({ message: '' })
    }
    onChangeNotif(success, message){
        this.setState({ success: success, message: message })
    }
    render() {
        const { t } = this.context
        const filterKey = this.state.filterKey
        const list = this.state.logs.filter(log => {
            if( filterKey == "all" ) {
                return log
            }
            else {
                return log.synchronizerAuditLog.status == filterKey
            }
        }).map((log, key) =>
            <RowDataset
                key={key}
                log={log}
                fetchMapping={() => this.fetchMapping() }
                onChangeNotif={ this.onChangeNotif } />)
        return (
            <div id="container" className="container-fluid">
                <H2>{ t('dashboard.title') }</H2>
                {renderIf(this.state.message)(
                    <Alert message={ t(this.state.message) } success={this.state.success} closeMethod={this.closeNotif}/>
                )}
                {renderIf(this.state.logs.length > 0) (
                    <div>
                        <div className="wrap-result">
                            {renderIf(list.length > 0) (
                                <table className="table table-sm">
                                    <thead className="thead-dark">
                                    <tr>
                                        <th>{t('dataset.label.name')}</th>
                                        <th>{t('dashboard.list.dataset_name')}</th>
                                        <th>{t('dashboard.list.dcmodel_name')}</th>
                                        <th>{t('dashboard.list.date')}</th>
                                        <th>{t('dashboard.list.actions')}</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                        {list}
                                    </tbody>
                                </table>
                            )}
                            {renderIf(list.length == 0) (
                                <div className="alert alert-info" role="alert">
                                    <p>{ t('dashboard.notif.no_data') }</p>
                                </div>
                            )}
                        </div>
                    </div>
                )}
                {renderIf(this.state.logs.length == 0) (
                    <div className="alert alert-info" role="alert">
                        <p><i>{ t('dashboard.notif.no_data') }</i></p>
                    </div>
                )}
            </div>
        )
    }
}

Dashboard.contextTypes = {
    csrfToken: PropTypes.string,
    csrfTokenHeaderName: PropTypes.string,
    t: PropTypes.func
}

export default translate(['dc-exporter'])(Dashboard)
