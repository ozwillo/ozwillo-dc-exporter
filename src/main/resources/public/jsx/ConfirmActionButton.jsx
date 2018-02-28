import React, {Component} from "react"
import { Modal, Button } from 'bootstrap'
import { translate } from 'react-i18next'


class ConfirmActionButton extends React.Component {
    static propTypes = {
        content: React.PropTypes.string.isRequired,
        onConfirm: React.PropTypes.func.isRequired,
        confirmLabel: React.PropTypes.string.isRequired,
        onHide: React.PropTypes.func.isRequired,
        show: React.PropTypes.bool.isRequired
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
        this.props.onHide()
    }
    confirm() {
        this.props.onConfirm()
        this.props.onHide()
    }
    render() {
        const { t } = this.context
        return (
            <Modal show={this.props.show} onHide={this.close} >
                <Modal.Header closeButton>
                    <Modal.Title >{ this.props.confirmLabel }</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    { this.props.content }
                </Modal.Body>
                <Modal.Footer>
                    <Button onClick={this.close}>{ t('action.close') }</Button>
                    <Button onClick={this.confirm} bsStyle="danger">{ t('action.confirm') }</Button>
                </Modal.Footer>
            </Modal>
        )
    }
}

export default translate(['dc-exporter'])(ConfirmActionButton)
