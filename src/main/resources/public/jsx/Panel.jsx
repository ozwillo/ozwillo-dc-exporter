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

const Panel = ({log}) =>
    <div className={'panel' + (!log.synchronizerAuditLog ? ' panel-warning' : log.synchronizerAuditLog.succeeded ? ' panel-success' : ' panel-danger')}>
        <div className="panel-heading">
            <div className="row">
                <div className="col-md-7">
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
                <div className="col-md-5">
                    <div className="text-right">
                        <DatasetLink datasetName={log.dcModelMapping.name} />
                        <ResourceLink datasetName={log.dcModelMapping.name} resourceId={log.dcModelMapping.ckanResourceId} />
                        <Link className="btn btn-default btn-xs panel-btn" to={`/dataset/${log.dcModelMapping.id}`}>
                            Modifier <span className="glyphicon glyphicon-pencil" aria-hidden="true"></span>
                        </Link>
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


const DatasetLink = ({ datasetName }) =>
    <a className="btn btn-default btn-xs panel-btn" target="_blank" href={'https://opendata.sictiam.fr/dataset/' + slugify(datasetName)}>
        Voir le jeu de données <span className="glyphicon glyphicon-new-window" aria-hidden="true"></span>
    </a>

const ResourceLink = ({ datasetName, resourceId }) =>
    <a className="btn btn-default btn-xs panel-btn" target="_blank" href={'https://opendata.sictiam.fr/dataset/' + slugify(datasetName) + '/resource/' + resourceId}>
        Voir la ressource <span className="glyphicon glyphicon-new-window" aria-hidden="true"></span>
    </a>

function slugify (text) {
    const a = 'àáäâèéëêìíïîòóöôùúüûñçßÿœæŕśńṕẃǵǹḿǘẍźḧ·/_,:;'
    const b = 'aaaaeeeeiiiioooouuuuncsyoarsnpwgnmuxzh------'
    const p = new RegExp(a.split('').join('|'), 'g')

    return text.toString().toLowerCase()
        .replace(/\s+/g, '-')           // Replace spaces with -
        .replace(p, c =>
            b.charAt(a.indexOf(c)))     // Replace special chars
        .replace(/&/g, '-and-')         // Replace & with 'and'
        .replace(/[^\w\-]+/g, '')       // Remove all non-word chars
        .replace(/\-\-+/g, '-')         // Replace multiple - with single -
        .replace(/^-+/, '')             // Trim - from start of text
        .replace(/-+$/, '')             // Trim - from end of text
}

module.exports = { ContainerPanel, PanelGroup, Panel }
