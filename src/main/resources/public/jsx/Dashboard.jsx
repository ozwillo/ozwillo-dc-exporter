import React from 'react'
import renderIf from 'render-if'
import {DropdownButton, MenuItem} from 'react-bootstrap'

import { ContainerPanel, PanelGroup, Panel } from './Panel'

export default class Dashboard extends React.Component {
    constructor(){
        super()
        this.state = {
            logs: [],
            filterKey: '',
            filterValue: 'Toutes les synchronisations'
        }
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
                <h1>Flux d'activités</h1>
                {renderIf(this.state.logs.length > 0) (
                    <div>
                        <DropdownButton title={ this.state.filterValue } onSelect={(eventKey) => this.onChangeFilter(eventKey)} id="filter-dropdown" >
                            <MenuItem eventKey={[ "Toutes les synchronisations", "" ]} >Toutes les synchronisations</MenuItem>
                            <MenuItem eventKey={[ "Synchronisations réussies", "valid" ]} >Synchronisations réussies</MenuItem>
                            <MenuItem eventKey={[ "Synchronisations non réussies", "error" ]}  >Synchronisations non réussies</MenuItem>
                            <MenuItem eventKey={[ "Synchronisations en cours", "pending" ]} >Synchronisations en cours</MenuItem>
                        </DropdownButton>
                        {renderIf(list.length > 0) (
                            <div>
                                <ContainerPanel>
                                    <PanelGroup>{list}</PanelGroup>
                                </ContainerPanel>
                            </div>
                        )}
                        {renderIf(list.length == 0) (
                            <div className="alert alert-info text-lowercase" role="alert">
                                <p><i>Aucune { this.state.filterValue }</i></p>
                            </div>
                        )}
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
