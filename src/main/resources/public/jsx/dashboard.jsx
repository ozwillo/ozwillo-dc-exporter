import React from 'react'

import { ContainerPanel, PanelGroup, Panel } from './Panel'

export default class Dashboard extends React.Component{

    constructor(props){
        super(props)
        this.state = { logs:[] }
    }

    componentDidMount(){
        fetch('/api/dc-model-mapping/logs', { credentials: 'same-origin'})
            .then(reponse => reponse.json())
            .then(json => this.setState({logs: json}))
    }

    render() {
        const logs = this.state.logs
        return (
            <div id="container">
                <h1>Dashboard</h1>
                <ContainerPanel>
                    <LogsList logs={logs}/>
                </ContainerPanel>
            </div>
        )
    }
}

const LogsList =  ({logs}) => {
    const list = logs.map(log =>
        <Panel key={log.dcModelMapping.name} log={log}/>
    )
    return(
        <PanelGroup> {list} </PanelGroup>
    )
}