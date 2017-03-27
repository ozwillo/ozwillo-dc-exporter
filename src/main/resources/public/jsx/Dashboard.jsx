import React from 'react'
import renderIf from 'render-if'
import {DropdownButton, MenuItem} from 'react-bootstrap'

import { ContainerPanel, PanelGroup, Panel } from './Panel'

export default class Dashboard extends React.Component {
    state = {
        logs: [],
        filterKey: '',
        filterValue: 'Tous les exports',
        success: true,
        message: ''
    }
    constructor(){
        super()
        this.onChangeFilter = this.onChangeFilter.bind(this)
    }
    componentDidMount() {
        fetch('/api/dc-model-mapping/logs', { credentials: 'same-origin'})
            .then(reponse => reponse.json())
            .then(json => this.setState({ logs: json }))
    }
    onChangeFilter(eventKey) {
        this.setState({ filterValue : eventKey[0], filterKey : eventKey[1] })
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
        }).map(log =>
            <Panel key={log.dcModelMapping.dcId} log={log}/>
        )
        return (
            <div id="container" className="container">
                <h1>Tableau de bord</h1>
                {renderIf(this.state.message)(
                    <Alert message={this.state.message} success={this.state.success} closeMethod={this.closeNotif}/>
                )}
                {renderIf(this.state.logs.length > 0) (
                    <div>
                        <div className="filter-dropdown text-right" >
                            <DropdownButton title={ this.state.filterValue } onSelect={(eventKey) => this.onChangeFilter(eventKey)} id="filter-dropdown" pullRight={true}>
                                <MenuItem eventKey={[ "Tous les exports", "" ]} >Tous les exports</MenuItem>
                                <MenuItem eventKey={[ "Exports réussis", "valid" ]} >Exports réussis</MenuItem>
                                <MenuItem eventKey={[ "Exports en erreur", "error" ]} >Exports en erreur</MenuItem>
                                <MenuItem eventKey={[ "Exports en cours", "pending" ]} >Exports en cours</MenuItem>
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
                                <NoExport />
                            )}
                        </div>
                    </div>
                )}
                {renderIf(this.state.logs.length == 0) (
                    <NoExport />
                )}
            </div>
        )
    }
}

const NoExport = () => (
    <div className="alert alert-info" role="alert">
        <p><i>Aucun export</i></p>
    </div>
)
