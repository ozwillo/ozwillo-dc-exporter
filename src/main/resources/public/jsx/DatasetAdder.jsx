import React from 'react'

import renderIf from 'render-if'

import { Form, FormGroup, Label, SelectField, InputText, SubmitButton } from './Form'

export default React.createClass({
    contextTypes: {
        csrfToken: React.PropTypes.string,
        csrfTokenHeaderName: React.PropTypes.string
    },
    getInitialState() {
        return {
            dcId: ''
        }
    },
    onDatasetSelected(dcId) {
        this.setState({ dcId })
    },
    registerDataset(fields) {
        fields['dcId'] = this.state.dcId
        fetch('/api/dc-model-mapping', {
            method: 'POST',
            credentials: 'same-origin',
            headers: {
                'Content-Type': 'application/json',
                [this.context.csrfTokenHeaderName] : this.context.csrfToken
            },
            body: JSON.stringify(fields)
        })
            .catch(error => console.log(error.message))
    },
    render() {
        return (
            <div>
                <h1>Dataset registration</h1>
                <DatasetChooser dcId={this.state.dcId} onDatasetSelected={this.onDatasetSelected} />
                {renderIf(this.state.dcId)(
                    <DatasetConfigurer onSubmit={this.registerDataset} />
                )}
            </div>
        )
    }
})

const DatasetChooser = React.createClass({
    getInitialState() {
        return {
            datasets: []
        }
    },
    componentDidMount() {
        fetch('/api/dc/models', { credentials: 'same-origin' })
            .then(response => response.json())
            .then(json => this.setState({ datasets: json }))
    },
    render() {
        const options = this.state.datasets.map(dataset =>
            <option key={dataset['@id']} value={dataset['@id']}>{dataset['dcmo:name']}</option>
        )
        return (
            <Form>
                <FormGroup>
                    <Label htmlFor="dcId" value="Choose a dataset" />
                    <SelectField id="dcId" options={options} value={this.props.dcId}
                                 onChange={(event) => this.props.onDatasetSelected(event.target.value)} />
                </FormGroup>
            </Form>
        )
    }
})

const DatasetConfigurer = React.createClass({
    getInitialState() {
        return {
            fields: {
                name: '',
                licence: '',
                frequency: ''
            }
        }
    },
    onFieldChange(id, value) {
        const fields = this.state.fields
        fields[id] = value
        this.setState({ fields })
    },
    render() {
        const frequencyOptions = ['Daily', 'Weekly', 'Monthly'].map(frequency =>
            <option key={frequency} value={frequency}>{frequency}</option>
        )
        return (
            <Form>
                <FormGroup>
                    <Label htmlFor="name" value="Name" />
                    <InputText id="name" value={this.state.fields['name']}
                               onChange={(event) => this.onFieldChange(event.target.id, event.target.value)}/>
                </FormGroup>
                <LicenceChooser currentLicence={this.state.fields['licence']}
                                onChange={(event) => this.onFieldChange(event.target.id, event.target.value)}/>
                <FormGroup>
                    <Label htmlFor="frequency" value="Frequency" />
                    <SelectField id="frequency" value={this.state.fields['frequency']}
                                 options={frequencyOptions}
                                 onChange={(event) => this.onFieldChange(event.target.id, event.target.value)}/>
                </FormGroup>
                <SubmitButton label="Create" onClick={(event) => this.props.onSubmit(this.state.fields)} />
            </Form>
        )
    }
})

const LicenceChooser = React.createClass({
    getInitialState() {
        return {
            licences: {}
        }
    },
    componentDidMount() {
        fetch('/api/ckan/licences', { credentials: 'same-origin' })
            .then(response => response.json())
            .then(json => this.setState({ licences: json }))

    },
    render() {
        const options = Object.keys(this.state.licences).map(key =>
            <option key={key} value={key}>{this.state.licences[key]}</option>
        )
        return (
            <FormGroup>
                <Label htmlForm="licence" value="Licence"/>
                <SelectField id="licence" value={this.props.currentLicence}
                    options={options} onChange={this.props.onChange} />
            </FormGroup>
        )
    }
})
