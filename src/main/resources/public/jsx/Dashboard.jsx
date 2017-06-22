import React from 'react'
import renderIf from 'render-if'
import { DropdownButton, MenuItem } from 'react-bootstrap'
import { translate } from 'react-i18next'

import { ContainerPanel, PanelGroup, Panel } from './Panel'
import { Alert } from './Form'

class Dashboard extends React.Component {
    state = {
        logs: [],
        filterKey: 'all',
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
        const list = this.state.logs.filter(function(log) {
            if( filterKey == "all" ) {
                return log
            }
            else {
                return log.synchronizerAuditLog.status == filterKey
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
                                <MenuItem eventKey={'all'} >{ t('dashboard.sort.all') }</MenuItem>
                                <MenuItem eventKey={'SUCCEEDED'} >{ t('dashboard.sort.SUCCEEDED') }</MenuItem>
                                <MenuItem eventKey={'FAILED'}  >{ t('dashboard.sort.FAILED') }</MenuItem>
                                <MenuItem eventKey={'PENDING'} >{ t('dashboard.sort.PENDING') }</MenuItem>
                                <MenuItem eventKey={'MODIFIED'} >{ t('dashboard.sort.MODIFIED') }</MenuItem>
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

export default translate(['dc-exporter'])(Dashboard)
