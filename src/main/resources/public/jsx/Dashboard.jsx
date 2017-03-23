import React from 'react'
import renderIf from 'render-if'
import { DropdownButton, MenuItem } from 'react-bootstrap'

import { ContainerPanel, PanelGroup, Panel } from './Panel'
import { Alert } from './Form'

export default class Dashboard extends React.Component {
    state = {
        logs: [],
        filterKey: '',
        filterValue: 'Toutes les synchronisations',
        success: true,
        message: ''
    }
    static contextTypes = {
        csrfToken: React.PropTypes.string,
        csrfTokenHeaderName: React.PropTypes.string
    }
    constructor(){
        super()
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
                <h1>Flux d'activités</h1>
                {renderIf(this.state.message)(
                    <Alert message={this.state.message} success={this.state.success} closeMethod={this.closeNotif}/>
                )}
                {renderIf(this.state.logs.length > 0) (
                    <div>
                        <div className="filter-dropdown text-right" >
                            <DropdownButton title={ this.state.filterValue } onSelect={(eventKey) => this.onChangeFilter(eventKey)} id="filter-dropdown" pullRight={true}>
                                <MenuItem eventKey={[ "Toutes les synchronisations", "" ]} >Toutes les synchronisations</MenuItem>
                                <MenuItem eventKey={[ "Synchronisations réussies", "valid" ]} >Synchronisations réussies</MenuItem>
                                <MenuItem eventKey={[ "Synchronisations non réussies", "error" ]}  >Synchronisations non réussies</MenuItem>
                                <MenuItem eventKey={[ "Synchronisations en cours", "pending" ]} >Synchronisations en cours</MenuItem>
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
                                    <p><i>Aucunes <span className="text-lowercase"> { this.state.filterValue } </span></i></p>
                                </div>
                            )}
                        </div>
                    </div>
                )}
                {renderIf(this.state.logs.length == 0) (
                    <div className="alert alert-info" role="alert">
                        <p><i>Aucun jeu de données enregistré</i></p>
                    </div>
                )}
            </div>
        )
    }
}
