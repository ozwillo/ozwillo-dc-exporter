import React, { Component } from "react"
import { translate } from 'react-i18next'


class ConfirmActionButton extends Component {
    static propTypes = {
        content: React.PropTypes.string.isRequired,
        onConfirm: React.PropTypes.func.isRequired,
        confirmLabel: React.PropTypes.string.isRequired
    }
    static contextTypes = {
        t: React.PropTypes.func
    }
    constructor(props, context) {
        super(props, context)
        this.close = this.close.bind(this)
        this.confirm = this.confirm.bind(this)
    }
    close() {
        this.props.modalToggle()
    }
    confirm() {
        this.props.onConfirm()
        this.props.modalToggle()
    }
    render() {
        const { t } = this.context
        return (
            <div
                id={this.props.labelledby}
                className="modal fade"
                tabIndex="-1"
                role="dialog"
                aria-labelledby="delete-confirmation-modal"
                aria-hidden="true">
                <div className="modal-dialog" role="document">
                    <div className="modal-content">
                        <div className="modal-header">
                            <h5 className="modal-title" id="delete-confirmation-modal">{this.props.confirmLabel}</h5>
                            <button type="button" className="close" data-dismiss="modal" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>
                        <div className="modal-body">
                            {this.props.content}
                        </div>
                        <div className="modal-footer">
                            <button type="button" className="btn btn-outline-primary" data-dismiss="modal">{t('action.close')}</button>
                            <button type="button" className="btn btn-danger" onClick={this.confirm}>{t('action.confirm')}</button>
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}

export default translate(['dc-exporter'])(ConfirmActionButton)
