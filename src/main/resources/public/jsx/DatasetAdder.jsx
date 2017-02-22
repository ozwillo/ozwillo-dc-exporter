import React from 'react'

import renderIf from 'render-if'

import DatasetAutosuggest from './DatasetAutosuggest'

import { TagAutosuggest, Tag } from './TagAutosuggest'

import Checkbox from './Checkbox'

import { Form, FormGroup, Label, SelectField, InputText, Textarea, SubmitButton, Alert } from './Form'

export default class DatasetAdder extends React.Component {
    constructor(props) {
        super(props)

        this.state = { dcId: '', type: '', datasets: [], dataset: [], licenses: {}, project: '', version: '', message: '', success: true}

        this.onDatasetSelected = this.onDatasetSelected.bind(this)
        this.registerDataset = this.registerDataset.bind(this)
        this.checkStatus = this.checkStatus.bind(this)
    }
    checkStatus(response) {
        if (response.status >= 200 && response.status < 300) {
            this.setState({ success : true })
            return response
        } else {
            this.setState({ success : false })
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
    }
    onDatasetSelected(dcId) {
        const dataset = this.state.datasets.find(function(dataset){
            return dataset['@id'] == dcId
        })
        this.setState({ type: dataset['dcmo:name'], version: dataset['o:version'],
                        project: dataset['dcmo:pointOfViewAbsoluteName'], dcId: dcId, dataset: dataset })
    }
    registerDataset(fields) {
        fields['dcId'] = this.state.dcId
        fields['type'] = this.state.type
        fields['project'] = this.state.project
        fields['version'] = this.state.version
        fetch('/api/dc-model-mapping/models', {
            method: 'POST',
            credentials: 'same-origin',
            headers: {
                'Content-Type': 'application/json',
                [this.context.csrfTokenHeaderName] : this.context.csrfToken
            },
            body: JSON.stringify(fields)
        })
        .then(this.checkStatus)
        .then(() => this.setState({ updated: true, message: 'La configuration a bien été créée' }))
        .catch(response => {
            response.text().then(text => this.setState({ message: text }))
        })
    }
    render() {
        return (
            <div  id="container" className="col-sm-10">
                <h1>Enregistrement d'un jeu de données</h1>
                <Form>
                    {renderIf(this.state.message)(
                        <FormGroup>
                            <Alert message={this.state.message} success={this.state.success} />
                        </FormGroup>
                    )}
                    <DatasetChooser dcId={this.state.dcId} onDatasetSelected={this.onDatasetSelected}
                                    datasets={this.state.datasets} />
                    {renderIf(this.state.dcId)(
                        <div>
                            <Version version={this.state.version} />
                            <DatasetConfigurer onSubmit={this.registerDataset} licenses={this.state.licenses}
                                               datasets={this.state.datasets} projects={this.state.projects}
                                               dataset={this.state.dataset}
                            />
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
                <Label htmlFor="dcId" value="Choisir un jeu de données"/>
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
                ckanPackageId: '',
                name: '',
                description: '',
                license: '',
                source: '',
                tags: [],
                excludedFields: []
            }
        }
        this.onFieldChange = this.onFieldChange.bind(this)
        this.handleAddition = this.handleAddition.bind(this)
        this.handleDelete = this.handleDelete.bind(this)
        this.onDatasetNameChange = this.onDatasetNameChange.bind(this)
        this.toggleCheckbox = this.toggleCheckbox.bind(this)
        this.selectedCheckboxes = new Set();
    }
    toggleCheckbox(label){
        if (this.selectedCheckboxes.has(label)) {
            const index = this.state.fields.excludedFields.findIndex((excludeField) => {return excludeField==label})
            let excludeFields = this.state.fields.excludedFields
            excludeFields.splice(index, 1)
            this.onFieldChange('excludedFields', excludeFields)
            this.selectedCheckboxes.delete(label)
        } else {
            let excludeFields = this.state.fields.excludedFields
            excludeFields.push(label)
            this.onFieldChange('excludedFields', excludeFields)
            this.selectedCheckboxes.add(label)
        }
    }
    onFieldChange(id, value) {
        const fields = this.state.fields
        fields[id] = value
        this.setState({ fields })
    }
    handleDelete(i) {
        let tags = this.state.fields.tags
        tags.splice(i, 1)
        this.onFieldChange('tags', tags)
    }
    handleAddition(tag) {
        let tags = this.state.fields.tags
        tags.push(tag)
        this.onFieldChange('tags', tags)
    }
    onDatasetNameChange(dataset) {
        this.onFieldChange('ckanPackageId', dataset.id)
        this.onFieldChange('name', dataset.title)
    }

    render() {
        const tags = this.state.fields.tags.map(( tag,key ) =>
            <Tag key={key} keyword={tag.name} remove={this.handleDelete} id={key} />)
        const fields = this.props.dataset['dcmo:globalFields'].map((field, key) =>
            <Checkbox label={field['dcmf:name']} handleCheckboxChange={this.toggleCheckbox} key={key} />)
        return (
            <div>
                <FormGroup>
                    <Label htmlFor="excludedFields" value="Exclure des champs" />
                    <div className="col-sm-9">
                        { fields }
                    </div>
                </FormGroup>
                <FormGroup>
                    <Label htmlFor="name" value="Nom du jeu de données"/>
                    <DatasetAutosuggest onSelect={ this.onDatasetNameChange }/>
                </FormGroup>
                <FormGroup>
                    <Label htmlFor="resourceName" value="Nom de la ressource" />
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
                <FormGroup>
                    <Label htmlFor="tags" value="Tags"/>
                    <TagAutosuggest onSelect={ this.handleAddition }/>
                    {renderIf(tags.length > 0) (
                            <div className="col-sm-10 col-sm-offset-2">
                                <ul className="list-group">
                                    {tags}
                                </ul>
                            </div>
                    )}
                </FormGroup>
                <SubmitButton label="Créer" onClick={(event) => this.props.onSubmit(this.state.fields)} />
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
            <Label htmlForm="license" value="Licence"/>
            <SelectField id="license" value={currentLicense} onChange={onChange}>
                {options}
            </SelectField>
        </FormGroup>
    )
}
