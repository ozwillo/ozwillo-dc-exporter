import React from 'react'

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
            <div id="container" className="col-sm-10">
                <h1>Flux d'activités</h1>
                <ContainerPanel>
                    <PanelGroup>{list}</PanelGroup>
                </ContainerPanel>
            </div>
        )
    }
}
