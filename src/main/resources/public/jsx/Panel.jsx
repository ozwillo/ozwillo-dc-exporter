import React from 'react'
import { Link } from 'react-router'

const ContainerPanel =  ({children}) =>
    <div>
        {children}
    </div>

const PanelGroup = ({children}) =>
    <div className="panel-group">
        {children}
    </div>

const Panel = ({log, onClickDelete}) =>
    <div className={'panel' + (!log.synchronizerAuditLog ? ' panel-warning' : log.synchronizerAuditLog.succeeded ? ' panel-success' : ' panel-danger')}>
        <div className="panel-heading">
            <div className="row">
                <div className="col-md-6">
                    <h3 className="panel-title">
                        {log.dcModelMapping.resourceName}
                        {log.synchronizerAuditLog &&
                            <span className={'glyphicon' + (log.synchronizerAuditLog.succeeded ? ' glyphicon-ok' : ' glyphicon-warning-sign')} aria-hidden="true"></span>
                        }
                        {!log.synchronizerAuditLog &&
                            <span className="text-right glyphicon glyphicon-time"></span>
                        }
                    </h3>
                </div>
                <div className="col-md-6">
                    <div className="text-right">
                        <PanelUrlBtn url={log.datasetUrl} text="Voir le jeu de données" />
                        <PanelUrlBtn url={log.resourceUrl} text="Voir la ressource" />
                        <Link className="btn btn-default btn-xs panel-btn" to={`/dataset/${log.dcModelMapping.id}`}>
                            Modifier <span className="glyphicon glyphicon-pencil" aria-hidden="true"></span>
                        </Link>
                        <button className="btn btn-default btn-xs panel-btn" onClick={() => onClickDelete(log.dcModelMapping) } >
                            Supprimer <span className="glyphicon glyphicon-remove" aria-hidden="true"></span>
                        </button>
                    </div>
                </div>
            </div>
        </div>
        <ul className="list-group">
            <li className="list-group-item">
                <label className="col-sm-3 control-label">Nom du jeu de données</label>{log.dcModelMapping.name}
            </li>
            <li className="list-group-item">
                <label className="col-sm-3 control-label">Modèle du cœur de données</label>{log.dcModelMapping.type}
            </li>
            {(log.synchronizerAuditLog) &&
                <li className="list-group-item">
                    <label className="col-sm-3 control-label">Date de
                        synchronisation</label>{new Date(log.synchronizerAuditLog.date).toLocaleString()}
                </li>
            }
            {(log.synchronizerAuditLog && !log.synchronizerAuditLog.succeeded) &&
                <li className="list-group-item">
                    <label className="col-sm-3 control-label">Message</label>{log.synchronizerAuditLog.errorMessage}
                </li>
            }
        </ul>
    </div>


const PanelUrlBtn = ({ url, text }) =>
    <a className="btn btn-default btn-xs panel-btn" target="_blank" href={url}>
        {text} <span className="glyphicon glyphicon-new-window" aria-hidden="true"></span>
    </a>

module.exports = { ContainerPanel, PanelGroup, Panel }
