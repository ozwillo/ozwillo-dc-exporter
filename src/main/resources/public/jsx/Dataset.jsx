import React from 'react'
import renderIf from 'render-if'

import DatasetForm from './DatasetForm'
import Checkbox from './Checkbox'
import { Form, FormGroup, Label, SelectField, InputText, Textarea, SubmitButton, Alert } from './Form'

export default class Dataset extends React.Component {
    constructor(props) {
        super(props)

        this.state = {
            datasets: [],
            dataset: [],
            licenses: {},
            message: '',
            success: true,
            fields: {
                dcId: '',
                type: '',
                project: '',
                version: '',
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

        this.onDatasetSelected = this.onDatasetSelected.bind(this)
        this.registerDataset = this.registerDataset.bind(this)
        this.checkStatus = this.checkStatus.bind(this)
        this.onFieldChange = this.onFieldChange.bind(this)
        this.onDatasetNameChange = this.onDatasetNameChange.bind(this)
        this.toggleCheckbox = this.toggleCheckbox.bind(this)
        this.closeNotif = this.closeNotif.bind(this)
        this.selectedCheckboxes = new Set();
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
        if (this.props.params.id) {
            fetch('/api/dc-model-mapping/model', { credentials: 'same-origin' })
                .then(response => response.json())
                .then(json => this.setState({datasets: json}))
        }

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
        const fields = this.state.fields
        fields['type'] = dataset['dcmo:name']
        fields['version'] = dataset['o:version']
        fields['project'] = dataset['dcmo:pointOfViewAbsoluteName']
        fields['dcId'] = dcId
        this.setState({ fields: fields, dataset: dataset })
    }
    registerDataset(fields) {
        fetch('/api/dc-model-mapping/model', {
            method: 'POST',
            credentials: 'same-origin',
            headers: {
                'Content-Type': 'application/json',
                [this.context.csrfTokenHeaderName] : this.context.csrfToken
            },
            body: JSON.stringify(fields)
        })
        .then(this.checkStatus)
        .then(() => this.setState({ updated: true, message: 'Dataset mapping created' }))
        .catch(response => {
            response.text().then(text => this.setState({ message: text }))
        })
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
    onDatasetNameChange(dataset) {
        this.onFieldChange('ckanPackageId', dataset.id)
        this.onFieldChange('name', dataset.title)
    }
    closeNotif(){
        this.setState({ message: '' })
    }
    render() {
        const fields = this.state.fields.dcId ?
            this.state.dataset['dcmo:globalFields'].map((field, key) =>
                <Checkbox label={field['dcmf:name']} handleCheckboxChange={this.toggleCheckbox} key={key} />)
            : null

        return (
            <div  id="container" className="container">
                <h1>Enregistrement d'un jeu de données</h1>
                {renderIf(this.state.message)(
                    <Alert message={this.state.message} success={this.state.success} closeMethod={this.closeNotif}/>
                )}
                <Form>
                    <div className="panel panel-default">
                        <div className="panel-heading">
                            <h3 className="panel-title">Modèle</h3>
                        </div>
                        <div className="panel-body">
                            <DatasetChooser dcId={this.state.fields.dcId} onDatasetSelected={this.onDatasetSelected}
                                            datasets={this.state.datasets} />
                            {renderIf(this.state.fields.dcId)(
                                <div>
                                    <Version version={this.state.fields.version} />
                                    <FormGroup>
                                        <Label htmlFor="excludedFields" value="Exclure des champs" />
                                        <div className="col-sm-9">
                                            { fields }
                                        </div>
                                    </FormGroup>
                                </div>
                            )}
                        </div>
                    </div>
                    {renderIf(this.state.fields.dcId)(
                        <div>
                            <DatasetForm onDatasetNameChange={this.onDatasetNameChange}
                                         onFieldChange={this.onFieldChange}
                                         source={this.state.fields.source}
                                         datasetName={this.state.fields.name}
                                         licenses={this.state.licenses}
                                         license={this.state.fields['license']}
                                         tags={this.state.fields.tags} />

                            <div className="panel panel-default">
                                <div className="panel-heading">
                                    <h3 className="panel-title">Ressource</h3>
                                </div>
                                <div className="panel-body">
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
                                </div>
                            </div>
                            <SubmitButton label="Créer" onClick={(event) => this.registerDataset(this.state.fields)} />
                        </div>
                    )}
                </Form>
            </div>
        )
    }
}

Dataset.contextTypes = {
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

Dataset.PropTypes = {
    onSubmit: React.PropTypes.func.isRequired
}
