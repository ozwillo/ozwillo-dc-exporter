import React, {Component} from "react"
import { Modal, Button } from 'react-bootstrap'


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
    constructor(props) {
        super(props)
        this.close = this.close.bind(this)
    }
    close() {
        this.setState({show : false})
        this.props.onHide()
    }
    render() {
        return (
            <Modal show={this.state.show} onHide={this.close} >
                <Modal.Header closeButton>
                    <Modal.Title >{ this.props.confirmLabel }</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    { this.props.content }
                </Modal.Body>
                <Modal.Footer>
                    <Button onClick={this.close}>Fermer</Button>
                    <Button onClick={this.props.onConfirm} bsStyle="danger">Confirmer</Button>
                </Modal.Footer>
            </Modal>
        )
    }
}