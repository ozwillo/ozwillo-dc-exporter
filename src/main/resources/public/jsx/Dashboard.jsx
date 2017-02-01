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
            <Panel key={log.dcModelMapping.name} log={log}/>
        )
        return (
            <div id="container">
                <h1>Dashboard</h1>
                <ContainerPanel>
                    <PanelGroup>{list}</PanelGroup>
                </ContainerPanel>
            </div>
        )
    }
}
