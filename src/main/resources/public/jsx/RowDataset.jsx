import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { Link } from 'react-router-dom'
import { translate } from 'react-i18next'
import format from 'date-fns/format'
import ConfirmActionButton from './ConfirmActionButton'

class RowDataset extends Component {
    state = {
        showActionButton: false
    }
    statusBadgeCss = {
        'SUCCEEDED': 'badge-success',
        'FAILED': 'badge-danger',
        'PENDING': 'badge-warning',
        'MODIFIED': 'badge-warning',
        '': 'badge-warning'
    }
    constructor(props, context) {
        super(props, context)
        this.checkStatus = this.checkStatus.bind(this)
        this.deleteMapping = this.deleteMapping.bind(this)
        this.modalToggle = this.modalToggle.bind(this)
        
    }
    componentDidMount() {
        $('[data-toggle="tooltip"]').tooltip()
    }
    checkStatus(response) {
        if (response.status >= 200 && response.status < 300) {
            return response
        } else {
            throw response
        }
    }
    modalToggle() {
        $(`#confirm-delete-${this.props.log.synchronizerAuditLog.id}`).modal('toggle');
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
    render() {
        const { t } = this.context
        const log = this.props.log
        return (
            <tr>
                <td>
                    <a href={`/dataset/${log.dcModelMapping.id}`}>
                        {log.dcModelMapping.resourceName}
                    </a>
                    <span
                        className=
                            {`badge ${this.statusBadgeCss[log.synchronizerAuditLog.status]} badge-pill ml-sm-1`}
                        data-toggle="tooltip" data-placement="top"
                        data-html="true"
                        title={(log.synchronizerAuditLog.status == 'FAILED' && log.synchronizerAuditLog.errorMessage) ?
                                t('dashboard.error.message', { message : log.synchronizerAuditLog.errorMessage}) + "<br/>" + t('dashboard.error.count', { count : log.synchronizerAuditLog.errorCount})  :
                                log.synchronizerAuditLog.status}>
                        {t(`dashboard.list.${log.synchronizerAuditLog.status}`)}
                    </span>
                    
                </td>
                <td>
                    <a href={log.datasetUrl} target="_blank">
                        {log.dcModelMapping.name}
                        <span className="glyphicon glyphicon-new-window" aria-hidden="true"/>
                    </a>
                </td>
                <td>
                    {log.dcModelMapping.type}
                </td>
                <td>
                    {format(new Date(log.synchronizerAuditLog.date), "DD/MM/YYYY HH:mm")}
                </td>
                <td>
                    <Link className="btn btn-sm btn-outline-primary mr-sm-1" to={`/dataset/${log.dcModelMapping.id}`}>
                        {t('action.edit')}
                    </Link>
                    <button type="button" className="btn btn-sm btn-outline-danger" onClick={() => this.modalToggle()} data-target={`#confirm-delete-${log.synchronizerAuditLog.id}`}>
                        {t('action.delete')}
                    </button>
                    <ConfirmActionButton
                        labelledby={`confirm-delete-${log.synchronizerAuditLog.id}`}
                        content={t('delete_confirmation.message', {name: log.dcModelMapping.resourceName})}
                        onConfirm={this.deleteMapping}
                        confirmLabel={t('delete_confirmation.title')}
                        modalToggle={this.modalToggle}/>
                </td>
            </tr>
        )
    }
}

export default translate(['dc-exporter'])(RowDataset)

RowDataset.propTypes = {
    log: PropTypes.object.isRequired,
    fetchMapping: PropTypes.func.isRequired,
    onChangeNotif: PropTypes.func.isRequired
}

RowDataset.contextTypes = {
    csrfToken: PropTypes.string,
    csrfTokenHeaderName: PropTypes.string,
    t: PropTypes.func
}
