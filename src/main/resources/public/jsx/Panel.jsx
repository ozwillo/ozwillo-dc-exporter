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
    <div className="panel panel-success">
        <div className="panel-heading">
            <div className="row">
                <div className="col-md-9">
                    <h3 className="panel-title">{log.name}</h3>
                </div>
                <div className="col-md-3">
                    {log.synchronizerAuditLog.date}<span className="glyphicon glyphicon-ok" aria-hidden="true"></span>
                </div>
            </div>
        </div>
            <ul className="list-group">
                <li className="list-group-item"><label className="col-sm-2 control-label">Ressource Name</label>{log.resourceName}</li>
                <li className="list-group-item"><label className="col-sm-2 control-label">Type DC</label>{log.type}</li>
            </ul>
    </div>

module.exports = { ContainerPanel, PanelGroup, Panel }
