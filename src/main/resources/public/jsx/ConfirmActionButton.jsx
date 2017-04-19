import React, {Component} from "react"
import { Modal, Button } from 'react-bootstrap'
import { translate } from 'react-i18next'


class ConfirmActionButton extends React.Component {
    static propTypes = {
        content: React.PropTypes.string.isRequired,
        onConfirm: React.PropTypes.func.isRequired,
        confirmLabel: React.PropTypes.string.isRequired,
        onHide: React.PropTypes.func.isRequired
    }
    static contextTypes = {
        t: React.PropTypes.func
    }
    state = {
        show: true
    }
    constructor(props, context) {
        super(props, context)
        this.close = this.close.bind(this)
    }
    close() {
        this.setState({show : false})
        this.props.onHide()
    }
    render() {
        const { t } = this.context
        return (
            <Modal show={this.state.show} onHide={this.close} >
                <Modal.Header closeButton>
                    <Modal.Title >{ this.props.confirmLabel }</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    { this.props.content }
                </Modal.Body>
                <Modal.Footer>
                    <Button onClick={this.close}>{ t('action.close') }</Button>
                    <Button onClick={this.props.onConfirm} bsStyle="danger">{ t('action.confirm') }</Button>
                </Modal.Footer>
            </Modal>
        )
    }
}

export default translate(['dc-exporter'])(ConfirmActionButton)