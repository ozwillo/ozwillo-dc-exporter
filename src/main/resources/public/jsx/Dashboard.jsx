import React from 'react'
import renderIf from 'render-if'
import { DropdownButton, MenuItem } from 'react-bootstrap'
import { translate } from 'react-i18next'

import { ContainerPanel, PanelGroup, Panel } from './Panel'
import { Alert } from './Form'

class Dashboard extends React.Component {
    state = {
        logs: [],
        filterKey: '',
        filterValue: 'dashboard.sort.all',
        success: true,
        message: ''
    }
    static contextTypes = {
        csrfToken: React.PropTypes.string,
        csrfTokenHeaderName: React.PropTypes.string,
        t: React.PropTypes.func
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
        this.setState({ filterValue : eventKey[0], filterKey : eventKey[1] })
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
        const list = this.state.logs.filter(function(log) {
            switch (filterKey) {
                case 'pending' :
                    return !log.synchronizerAuditLog
                case 'valid' :
                    return log.synchronizerAuditLog.succeeded == true
                case 'error' :
                    return log.synchronizerAuditLog.succeeded == false
                default:
                    return log
            }
        }).map(log => <Panel key={log.dcModelMapping.dcId} log={log} fetchMapping={() => this.fetchMapping() }
                   onChangeNotif={ this.onChangeNotif } />
        )
        return (
            <div id="container" className="container">
                <h1>{ t('dashboard.title') }</h1>
                {renderIf(this.state.message)(
                    <Alert message={ t(this.state.message) } success={this.state.success} closeMethod={this.closeNotif}/>
                )}
                {renderIf(this.state.logs.length > 0) (
                    <div>
                        <div className="filter-dropdown text-right" >
                            <DropdownButton title={ t(this.state.filterValue) } onSelect={(eventKey) => this.onChangeFilter(eventKey)} id="filter-dropdown" pullRight={true}>
                                <MenuItem eventKey={[ "dashboard.sort.all", "" ]} >{ t('dashboard.sort.all') }</MenuItem>
                                <MenuItem eventKey={[ "dashboard.sort.valid", "valid" ]} >{ t('dashboard.sort.valid') }</MenuItem>
                                <MenuItem eventKey={[ "dashboard.sort.error", "error" ]}  >{ t('dashboard.sort.error') }</MenuItem>
                                <MenuItem eventKey={[ "dashboard.sort.pending", "pending" ]} >{ t('dashboard.sort.pending') }</MenuItem>
                            </DropdownButton>
                        </div>
                        <div className="wrap-result">
                            {renderIf(list.length > 0) (
                                <div>
                                    <ContainerPanel>
                                        <PanelGroup>{list}</PanelGroup>
                                    </ContainerPanel>
                                </div>
                            )}
                            {renderIf(list.length == 0) (
                                <div className="alert alert-info" role="alert">
                                    <p><i>{ t('dashboard.notif.no_data_sort') }<span className="text-lowercase"> { t(this.state.filterValue) } </span></i></p>
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

export default translate(['dc-exporter'])(Dashboard)
