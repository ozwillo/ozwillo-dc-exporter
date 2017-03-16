import React from 'react'
import renderIf from 'render-if'
import { browserHistory } from 'react-router';
import { DropdownButton, MenuItem } from 'react-bootstrap'

import { ContainerPanel, PanelGroup, Panel } from './Panel'
import { Alert } from './Form'
import ConfirmActionButton from './ConfirmActionButton'

export default class Dashboard extends React.Component {
    state = {
        logs: [],
        filterKey: '',
        filterValue: 'Toutes les synchronisations',
        showConfirmActionButton: false,
        mappingSelected: {},
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
        this.onClickDelete = this.onClickDelete.bind(this)
        this.returnConfirmAction = this.returnConfirmAction.bind(this)
        this.deleteMapping = this.deleteMapping.bind(this)
        this.closeNotif = this.closeNotif.bind(this)
        this.checkStatus = this.checkStatus.bind(this)
    }
    checkStatus(response) {
        if (response.status >= 200 && response.status < 300) {
            this.setState({ success : true })
            return response
        } else {
            this.setState({ success : false })
            throw response
        }
    }
    componentDidMount() {
        fetch('/api/dc-model-mapping/logs', { credentials: 'same-origin'})
            .then(reponse => reponse.json())
            .then(json => this.setState({ logs: json }))
    }
    onChangeFilter(eventKey) {
        this.setState({ filterValue : eventKey[0], filterKey : eventKey[1] })
    }
    deleteMapping(){
        fetch('/api/dc-model-mapping/model/' + this.state.mappingSelected.id, {
            credentials: 'same-origin',
            method: 'DELETE',
            headers: {
                [this.context.csrfTokenHeaderName] : this.context.csrfToken
            }
        })
        .then(this.checkStatus)
        .then(response => response.text())
        .then(id => {
            const logs = this.state.logs.filter((log) => log.dcModelMapping.id == id)
            this.setState({ message: 'La synchronisation de la ressource a été supprimée', logs: logs })
        })
        .catch(response => {
            response.text().then(text => this.setState({ message: text }))
        })
        this.returnConfirmAction()
    }
    onClickDelete(dcModelMapping){
        this.setState({ showConfirmActionButton: true, mappingSelected : dcModelMapping})
    }
    returnConfirmAction(){
        this.setState({ showConfirmActionButton: false , mappingSelected: {}})
    }
    closeNotif(){
        this.setState({ message: '' })
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
            <Panel key={log.dcModelMapping.dcId} log={log} onClickDelete={ this.onClickDelete } />
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
                {renderIf(this.state.showConfirmActionButton) (
                    <ConfirmActionButton
                        content={"Vous êtes sur le point de supprimer la synchronisation de la ressource : " + this.state.mappingSelected.resourceName}
                        onConfirm={ this.deleteMapping }
                        confirmLabel="Suppression"
                        onHide={ this.returnConfirmAction }/>
                )}
            </div>
        )
    }
}
