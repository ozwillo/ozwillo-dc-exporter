import React from 'react'

import renderIf from 'render-if'

import { WithContext as ReactTags } from 'react-tag-input'

import Autosuggest from 'react-bootstrap-autosuggest'

import tagStyles from '../reactTags.css'

import { Form, FormGroup, Label, SelectField, InputText, Textarea, SubmitButton, Alert } from './Form'

export default class DatasetAdder extends React.Component {
    constructor(props) {
        super(props)

        this.state = { dcId: '', type: '', datasets: [], licenses: {}, project: '', suggestions: [], version: '', message: '', success: ''}

        this.onDatasetSelected = this.onDatasetSelected.bind(this)
        this.registerDataset = this.registerDataset.bind(this)
        this.checkStatus = this.checkStatus.bind(this)
    }
    checkStatus(response) {
        if (response.status >= 200 && response.status < 300) {
            this.setState({success : "alert-success"})
            return response
        } else {
            this.setState({success : "alert-danger"})
            throw response
        }
    }
    componentDidMount() {
        fetch('/api/dc/models', { credentials: 'same-origin' })
            .then(response => response.json())
            .then(json => this.setState({datasets: json}))

        fetch('/api/ckan/licences', {credentials: 'same-origin'})
            .then(response => response.json())
            .then(json => this.setState({licenses: json}))

        fetch('/api/ckan/tags', {credentials: 'same-origin'})
            .then(response => response.json())
            .then(json => this.setState({suggestions: json}))
    }
    onDatasetSelected(dcId) {
        const dataset = this.state.datasets.find(function(dataset){
            return dataset['@id'] == dcId
        })
        this.setState({ type: dataset['dcmo:name'], version: dataset['o:version'], project: dataset['dcmo:pointOfViewAbsoluteName'], dcId: dcId })
    }
    registerDataset(fields) {
        fields['dcId'] = this.state.dcId
        fields['type'] = this.state.type
        fields['project'] = this.state.project
        fields['version'] = this.state.version
        fields['tags'] = fields['tags'].map(tag => {
                    return {name: tag.text}
        })
        fetch('/api/dc-model-mapping', {
            method: 'POST',
            credentials: 'same-origin',
            headers: {
                'Content-Type': 'application/json',
                [this.context.csrfTokenHeaderName] : this.context.csrfToken
            },
            body: JSON.stringify(fields)
        })
            .then(this.checkStatus)
            .then(() => this.setState({ updated: true, message: 'Dataset mapping created'}))
            .catch(json => {
                json.json().then(text => this.setState({ message: text.error.name[0]}))
            })
    }
    render() {
        return (
            <div  id="container" className="col-sm-10">
                <h1>Dataset registration</h1>
                <Form>
                    {renderIf(this.state.success || this.state.message)(
                        <FormGroup>
                            <Alert message={this.state.message} status={this.state.success} />
                        </FormGroup>
                    )}
                    <DatasetChooser dcId={this.state.dcId} onDatasetSelected={this.onDatasetSelected}
                                    datasets={this.state.datasets} />
                    {renderIf(this.state.dcId)(
                        <div>
                            <Version version={this.state.version} />
                            <DatasetConfigurer onSubmit={this.registerDataset} licenses={this.state.licenses}
                                               datasets={this.state.datasets} projects={this.state.projects}
                                               suggestions={this.state.suggestions} />
                        </div>
                    )}
                </Form>
            </div>
        )
    }
}

DatasetAdder.contextTypes = {
    csrfToken: React.PropTypes.string,
    csrfTokenHeaderName: React.PropTypes.string
}

const DatasetChooser = ({ datasets, dcId, onDatasetSelected }) => {
    const options = datasets.map(dataset =>
        <option key={dataset['@id']} value={dataset['@id']}>{dataset['dcmo:name']}</option>
    )
    return (
            <FormGroup>
                <Label htmlFor="dcId" value="Choose a dataset"/>
                <SelectField id="dcId" value={dcId}
                             onChange={(event) => onDatasetSelected(event.target.value)}>
                    {options}
                </SelectField>
            </FormGroup>
    )
}

DatasetChooser.propTypes = {
    dcId: React.PropTypes.string.isRequired,
    onDatasetSelected: React.PropTypes.func.isRequired,
    datasets: React.PropTypes.array.isRequired
}

const Version = ({ version }) => {
    return (
            <FormGroup>
                <Label htmlFor="version" value="Version" />
                <InputText id="version" value={version}/>
            </FormGroup>
    )
}

class DatasetConfigurer extends React.Component {
    constructor(props) {
        super(props)

        this.state = {
            fields: {
                resourceName: '',
                name: '',
                description: '',
                license: '',
                source: '',
                tags: []
            },
            datasetsCkan: []
        }
        this.onFieldChange = this.onFieldChange.bind(this)
        this.handleAddition = this.handleAddition.bind(this)
        this.handleDelete = this.handleDelete.bind(this)
        this.onNameChange = this.onNameChange.bind(this)
    }
    componentDidMount() {
        fetch('/api/ckan/datasets', {credentials: 'same-origin'})
            .then(response => response.json())
            .then(json => this.setState({datasetsCkan: json}))
    }
    onFieldChange(id, value) {
        const fields = this.state.fields
        fields[id] = value
        this.setState({ fields })
    }
    handleDelete(i) {
        let tags = this.state.fields['tags']
        tags.splice(i, 1)
        this.onFieldChange('tags', tags)
    }
    handleAddition(tag) {
        let tags = this.state.fields['tags']
        tags.push({
            id: tags.length + 1,
            text: tag
        })
        this.onFieldChange('tags', tags)
    }
    onNameChange(value){
        this.onFieldChange("name",value)
    }

    render() {
        return (
            <div>
                <InputAutocomplete
                    suggestions={this.state.datasetsCkan}
                    onChange={this.onNameChange}
                    value={this.state.fields.name}
                />
                <FormGroup>
                    <Label htmlFor="resourceName" value="Resource Name" />
                    <InputText id="resourceName" value={this.state.fields.resourceName}
                               onChange={(event) => this.onFieldChange(event.target.id, event.target.value)}/>
                </FormGroup>
                <FormGroup>
                    <Label htmlFor="description" value="Description" />
                    <Textarea id="description" value={this.state.fields.description}
                              onChange={(event) => this.onFieldChange(event.target.id, event.target.value)}/>
                </FormGroup>
                <FormGroup>
                    <Label htmlFor="source" value="Source" />
                    <InputText id="source" value={this.state.fields.source}
                               onChange={(event) => this.onFieldChange(event.target.id, event.target.value)}/>
                </FormGroup>
                <LicenceChooser licenses={this.props.licenses} currentLicense={this.state.fields['license']}
                                onChange={(event) => this.onFieldChange(event.target.id, event.target.value)}/>
                <TagAutocomplete tags={this.state.fields['tags']} suggestions={this.props.suggestions}
                                handleAddition={this.handleAddition} handleDelete={this.handleDelete} />
                <SubmitButton label="Create" onClick={(event) => this.props.onSubmit(this.state.fields)} />
            </div>
        )
    }
}

DatasetAdder.PropTypes = {
    onSubmit: React.PropTypes.func.isRequired
}

const LicenceChooser = ({ licenses, currentLicense, onChange }) => {
    const options = Object.keys(licenses).map(key =>
        <option key={key} value={key}>{licenses[key]}</option>
    )
    return (
        <FormGroup>
            <Label htmlForm="license" value="License"/>
            <SelectField id="license" value={currentLicense} onChange={onChange}>
                {options}
            </SelectField>
        </FormGroup>
    )
}

const TagAutocomplete = ({ tags, suggestions, handleDelete, handleAddition}) => {
    let tabSuggestions = Object.keys(suggestions).map(key =>
        suggestions[key]
    )
    return (
        <FormGroup>
            <Label htmlFor="Tag" value="Tags"/>
            <ReactTags tags={tags}
                       suggestions={tabSuggestions}
                       handleDelete={handleDelete}
                       handleAddition={handleAddition}/>
        </FormGroup>
    )
}

const InputAutocomplete = ({ suggestions, value, onChange}) => {
    return (
        <FormGroup>
            <Label htmlFor="name" value="Name"/>
            <div className="col-sm-10">
                <Autosuggest
                    datalist={suggestions}
                    onChange={onChange}
                    value={value}
                    className={"col-sm-10"}
                />
            </div>
        </FormGroup>
    )
}

InputAutocomplete.PropTypes = {
    suggestions: React.PropTypes.array.isRequired,
    value: React.PropTypes.string.isRequired,
    onChange: React.PropTypes.func.isRequired
}