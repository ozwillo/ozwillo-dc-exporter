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
    <div className={'panel' + (log.synchronizerAuditLog && log.synchronizerAuditLog.succeeded ? ' panel-success' : ' panel-danger')}>
        <div className="panel-heading">
            <div className="row">
                <div className="col-md-9">
                    <h3 className="panel-title">{log.dcModelMapping.name}</h3>
                </div>
                <div className="col-md-3">
                    {log.synchronizerAuditLog &&
                        <div className="text-right">
                            {new Date(log.synchronizerAuditLog.date).toLocaleString()}
                            <span className={'glyphicon' + (log.synchronizerAuditLog.succeeded ? ' glyphicon-ok' : ' glyphicon-warning-sign')} aria-hidden="true"></span>
                        </div>
                    }
                    {!log.synchronizerAuditLog &&
                        <div className="text-right">
                            <span className="text-right glyphicon glyphicon-time"></span>
                        </div>
                    }
                </div>
            </div>
        </div>
        <ul className="list-group">
            <li className="list-group-item">
                <label className="col-sm-3 control-label">Nom de la ressource</label>{log.dcModelMapping.resourceName}
            </li>
            <li className="list-group-item">
                <label className="col-sm-3 control-label">Type DC</label>{log.dcModelMapping.type}
            </li>
            {(log.synchronizerAuditLog && !log.synchronizerAuditLog.succeeded) &&
                <li className="list-group-item">
                    <label className="col-sm-3 control-label">Message</label>{log.synchronizerAuditLog.errorMessage}
                </li>
            }
        </ul>
    </div>

module.exports = { ContainerPanel, PanelGroup, Panel }
