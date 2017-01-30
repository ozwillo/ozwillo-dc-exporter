import React from 'react'

const ContainerPanel =  ({children}) =>
    <div className="container">
        {children}
    </div>

const PanelGroup = ({children}) =>
    <div className="panel-group">
        {children}
    </div>

const Panel = ({log}) =>
    <div className={"panel panel-success"}>
        <div className="panel-heading">
            <div className="row">
                <div className="col-md-9">
                    <h3 className="panel-title">{log.dcModelMapping.name}</h3>
                </div>
                <div className="col-md-3">
                    {new Date(log.synchronizerAuditLog.date).toUTCString()}<span className={"glyphicon glyphicon-ok"} aria-hidden="true"></span>
                </div>
            </div>
        </div>
        <ul className="list-group">
            <li className="list-group-item"><label className="col-sm-3 control-label">Ressource Name</label>{log.dcModelMapping.resourceName}</li>
            <li className="list-group-item"><label className="col-sm-3 control-label">Type DC</label>{log.dcModelMapping.type}</li>
        </ul>
    </div>

module.exports = { ContainerPanel, PanelGroup, Panel }