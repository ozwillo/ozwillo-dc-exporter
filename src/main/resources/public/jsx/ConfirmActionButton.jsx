import React, {Component} from "react"
import { Modal, Header, Body, Footer, Button } from 'react-bootstrap'


export default class ConfirmActionButton extends React.Component {
    static propTypes = {
        content: React.PropTypes.string.isRequired,
        onConfirm: React.PropTypes.func.isRequired,
        confirmLabel: React.PropTypes.string.isRequired,
        onHide: React.PropTypes.func.isRequired
    }
    state = {
        show: true
    }
    render() {
        const close = () => {
            this.setState({show : false})
            this.props.onHide()
        }

        return (
            <Modal show={this.state.show} onHide={close} >
                <Modal.Header closeButton>
                    <Modal.Title >{ this.props.confirmLabel }</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    { this.props.content }
                </Modal.Body>
                <Modal.Footer>
                    <Button onClick={close}>Fermer</Button>
                    <Button onClick={this.props.onConfirm} bsStyle="danger">Confirmer</Button>
                </Modal.Footer>
            </Modal>
        )
    }
}