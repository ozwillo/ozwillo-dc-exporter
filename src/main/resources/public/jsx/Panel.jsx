import React, { Component } from 'react'
import { Link } from 'react-router'
import renderIf from 'render-if'

import ConfirmActionButton from './ConfirmActionButton'

class Panel extends Component {
    static propTypes = {
        log: React.PropTypes.object.isRequired,
        fetchMapping: React.PropTypes.func.isRequired,
        onChangeNotif: React.PropTypes.func.isRequired
    }
    static contextTypes = {
        csrfToken: React.PropTypes.string,
        csrfTokenHeaderName: React.PropTypes.string,
        t: React.PropTypes.func
    }
    state = {
        showActionButton: false
    }
    constructor(props, context) {
        super(props, context)
        this.checkStatus = this.checkStatus.bind(this)
        this.onClickDelete = this.onClickDelete.bind(this)
        this.deleteMapping = this.deleteMapping.bind(this)
        this.returnConfirmAction = this.returnConfirmAction.bind(this)
    }

    checkStatus(response) {
        if (response.status >= 200 && response.status < 300) {
            return response
        } else {
            throw response
        }
    }
    onClickDelete() {
        this.setState({ showActionButton: true })
    }
    deleteMapping(){
        fetch('/api/dc-model-mapping/model/' + this.props.log.dcModelMapping.id, {
            credentials: 'same-origin',
            method: 'DELETE',
            headers: {
                [this.context.csrfTokenHeaderName] : this.context.csrfToken
            }
        })
        .then(this.checkStatus)
        .then(response => {
            this.props.onChangeNotif(true, 'dashboard.notif.is_deleted')
            this.props.fetchMapping()
        })
        .catch(response => {
            response.text().then(text => this.props.onChangeNotif(false, text ))
            this.props.fetchMapping()
        })
    }
    returnConfirmAction(){
        this.setState({ showActionButton: false })
    }

    render() {
        const { t } = this.context
        const log = this.props.log
        return (
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
                                <PanelUrlBtn url={log.datasetUrl} text={ t('dashboard.panel.to_dataset_link') } />
                                <PanelUrlBtn url={log.resourceUrl} text={ t('dashboard.panel.to_resource_link') } />
                                <Link className="btn btn-default btn-xs panel-btn" to={`/dataset/${log.dcModelMapping.id}`}>
                                    { t('action.edit') } <span className="glyphicon glyphicon-pencil" aria-hidden="true"></span>
                                </Link>
                                <button className="btn btn-default btn-xs panel-btn" onClick={ this.onClickDelete } >
                                    { t('action.delete') } <span className="glyphicon glyphicon-remove" aria-hidden="true"></span>
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
                <ul className="list-group">
                    <li className="list-group-item">
                        <label className="col-sm-3 control-label">{ t('dashboard.panel.dataset_name') }</label>{log.dcModelMapping.name}
                    </li>
                    <li className="list-group-item">
                        <label className="col-sm-3 control-label">{ t('dashboard.panel.dcmodel_name') }</label>{log.dcModelMapping.type}
                    </li>
                    {(log.synchronizerAuditLog) &&
                    <li className="list-group-item">
                        <label className="col-sm-3 control-label">{ t('dashboard.panel.synchronization_date') }
                            </label>{new Date(log.synchronizerAuditLog.date).toLocaleString()}
                    </li>
                    }
                    {(log.synchronizerAuditLog && !log.synchronizerAuditLog.succeeded) &&
                    <li className="list-group-item">
                        <label className="col-sm-3 control-label">{ t('data.message') }</label>{log.synchronizerAuditLog.errorMessage}
                    </li>
                    }
                </ul>
                {renderIf(this.state.showActionButton) (
                    <ConfirmActionButton
                        content={t('delete_confirmation.message') + log.dcModelMapping.resourceName}
                        onConfirm={ this.deleteMapping }
                        confirmLabel={ t('delete_confirmation.title') }
                        onHide={ this.returnConfirmAction }/>
                )}
            </div>
        )
    }
}

const ContainerPanel =  ({children}) =>
    <div>
        {children}
    </div>

const PanelGroup = ({children}) =>
    <div className="panel-group">
        {children}
    </div>

const PanelUrlBtn = ({ url, text }) =>
    <a className="btn btn-default btn-xs panel-btn" target="_blank" href={url}>
        {text} <span className="glyphicon glyphicon-new-window" aria-hidden="true"></span>
    </a>

module.exports = { ContainerPanel, PanelGroup, Panel }
