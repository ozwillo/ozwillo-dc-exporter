import React from 'react'
import renderIf from 'render-if'

import { ContainerPanel, PanelGroup, Panel } from './Panel'

export default class Dashboard extends React.Component {
    state = {
        logs: []
    }
    componentDidMount() {
        fetch('/api/dc-model-mapping/logs', { credentials: 'same-origin'})
            .then(reponse => reponse.json())
            .then(json => this.setState({ logs: json }))
    }
    render() {
        const list = this.state.logs.map(log =>
            <Panel key={log.dcModelMapping.dcId} log={log}/>
        )
        return (
            <div id="container" className="container">
                <h1>Tableau de bord</h1>
                {renderIf(list.length > 0) (
                    <ContainerPanel>
                        <PanelGroup>{list}</PanelGroup>
                    </ContainerPanel>
                )}
                {renderIf(list.length == 0) (
                    <div className="alert alert-info" role="alert">
                        <p><i>Aucun jeu de données enregistré</i></p>
                    </div>
                )}
            </div>
        )
    }
}
