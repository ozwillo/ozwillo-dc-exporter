import React from 'react'

import { ContainerPanel, PanelGroup, Panel } from './Panel'

export default class Dashboard extends React.Component{

    constructor(props){
        super(props)
        this.state = { logs:[] }
    }

    componentDidMount(){
        fetch('/api/dc-exporter/logs', { credentials: 'same-origin'})
            .then(reponse => reponse.json())
            .then(json => this.setState({logs: json}))
    }

    render() {
        const logs = this.state.logs
        return (
            <div>
                <h1>Dashboard</h1>
                <ContainerPanel>
                    <LogsList logs={logs}/>
                </ContainerPanel>
            </div>
        )
    }
}

const LogsList =  ({logs}) => {
    console.log(logs)
    for(var log of logs){
        log.synchronizerAuditLog.date = new Date(log.synchronizerAuditLog.date).toUTCString()
        console.log(log.synchronizerAuditLog.date)
    }
    const list = logs.map(log =>
        <Panel key={log.name} log={log}/>
    )
    return(
        <PanelGroup> {list} </PanelGroup>
    )
}
