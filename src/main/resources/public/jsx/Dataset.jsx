import React from 'react'
import renderIf from 'render-if'
import { browserHistory } from 'react-router';

import DatasetForm from './DatasetForm'
import Checkbox from './Checkbox'
import { Form, FormGroup, Label, SelectField, InputText, Textarea, SubmitButton, Alert, ReadOnlyField } from './Form'

export default class Dataset extends React.Component {
    constructor(props) {
        super(props)

        this.state = {
            datasets: [],
            datasetsFetched: false,
            dataset: [],
            datasetFetched: false,
            newDataset: false,
            licenses: {},
            message: '',
            success: true,
            mode: 'create',
            fieldsFetched: false,
            fields: {
                dcId: '',
                type: '',
                project: '',
                version: '',
                resourceName: '',
                ckanPackageId: '',
                name: '',
                notes: '',
                description: '',
                license: '',
                source: '',
                tags: [],
                excludedFields: []
            }
        }

        this.loadMapping = this.loadMapping.bind(this)
        this.onDatasetSelected = this.onDatasetSelected.bind(this)
        this.registerDataset = this.registerDataset.bind(this)
        this.checkStatus = this.checkStatus.bind(this)
        this.onFieldChange = this.onFieldChange.bind(this)
        this.onDatasetNameChange = this.onDatasetNameChange.bind(this)
        this.toggleCheckbox = this.toggleCheckbox.bind(this)
        this.toggleNewDataset = this.toggleNewDataset.bind(this)
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
            this.loadMapping(this.props.params.id)
        }

        fetch('/api/dc/models', { credentials: 'same-origin' })
            .then(response => response.json())
            .then(json => this.setState({datasets: json, datasetsFetched: true}))

        fetch('/api/ckan/licences', {credentials: 'same-origin'})
            .then(response => response.json())
            .then(json => this.setState({licenses: json}))
    }
    componentDidUpdate() {
        if(this.props.params.id && this.state.fieldsFetched && this.state.datasetsFetched && !this.state.datasetFetched) {
            const dcId = this.state.fields.dcId
            const dataset = this.state.datasets.find(function(dataset){
                return dataset['@id'] == dcId
            })
            this.setState({ dataset: dataset, datasetFetched: true })
        }
    }
    loadMapping(id){
        fetch('/api/dc-model-mapping/model/' + id, { credentials: 'same-origin' })
            .then(response => response.json())
            .then(json => this.setState({fields: json, mode: 'update', fieldsFetched: true, newDataset: false}))
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
        this.setState({ fields: fields, dataset: dataset, datasetFetched: true })
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
        .then(response => response.text())
        .then(id => {
            browserHistory.push('/dataset/' + id)
            this.loadMapping(id)
            this.setState({ message: 'Le jeu de données a été créé' })
        })
        .catch(text => this.setState({ message: text }))
    }
    updateDataset(fields) {
        fetch('/api/dc-model-mapping/model', {
            method: 'PUT',
            credentials: 'same-origin',
            headers: {
                'Content-Type': 'application/json',
                [this.context.csrfTokenHeaderName] : this.context.csrfToken
            },
            body: JSON.stringify(fields)
        })
        .then(this.checkStatus)
        .then(() => this.setState({ message: 'Le jeu de données a été mis à jour' }))
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
    toggleNewDataset(){
        const bool = !this.state.newDataset
        const fields = this.state.fields
        fields['name'] = ''
        fields['notes'] = ''
        fields['source'] = ''
        fields['tags'] = []
        fields['license'] = ''
        fields['ckanPackageId'] = ''
        this.setState({newDataset: bool, fields: fields})
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
        const fields = this.state.mode == 'create' && this.state.datasetFetched && this.state.fields.dcId ?
            this.state.dataset['dcmo:globalFields'].map((field, key) =>
                <Checkbox label={field['dcmf:name']} handleCheckboxChange={this.toggleCheckbox} key={key} />)
            : null

        const disabled = this.state.fields.name == null || this.state.fields.name == '' ? true
            : false

        const isModeCreate = renderIf(this.state.mode == 'create')
        const isModeUpdate = renderIf(this.state.mode == 'update')

        return (
            <div  id="container" className="container">
                <h1>Enregistrement d'un jeu de données</h1>
                {renderIf(this.state.message)(
                    <Alert message={this.state.message} success={this.state.success} closeMethod={this.closeNotif}/>
                )}
                <Form>
                    <div className="panel panel-default">
                        <div className="panel-heading">
                            <h3 className="panel-title">Cœur de données</h3>
                        </div>
                        <div className="panel-body">
                            {isModeCreate(
                                <DatasetChooser dcId={this.state.fields.dcId} onDatasetSelected={this.onDatasetSelected}
                                    datasets={this.state.datasets} />
                            )}
                            {isModeUpdate(
                                <FormGroup>
                                    <Label htmlFor="model" value="Modèle" />
                                    <ReadOnlyField id="model" value={this.state.fields.type} />
                                </FormGroup>
                            )}
                            {renderIf(this.state.fields.dcId)(
                                <div>
                                    <FormGroup>
                                        <Label htmlFor="version" value="Version" />
                                        {isModeCreate(<InputText id="version" value={this.state.fields.version}/>)}
                                        {isModeUpdate(<ReadOnlyField id="version" value={this.state.fields.version}/>)}
                                    </FormGroup>
                                    {isModeCreate(
                                        <FormGroup>
                                            <Label htmlFor="excludedFields" value="Champs à exporter" />
                                            <div className="col-sm-9">
                                                { fields }
                                            </div>
                                        </FormGroup>
                                    )}
                                </div>
                            )}
                        </div>
                    </div>
                    {renderIf(this.state.fields.dcId)(
                        <div>
                            <DatasetForm onChange={this.onFieldChange}
                                         onDatasetNameChange={this.onDatasetNameChange}
                                         onFieldChange={this.onFieldChange}
                                         newDataset={this.state.newDataset}
                                         toggleNewDataset={this.toggleNewDataset}
                                         source={this.state.fields.source}
                                         notes={this.state.fields.notes}
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
                            {isModeCreate(<SubmitButton label="Créer" onClick={(event) => this.registerDataset(this.state.fields)} disabled={disabled} />)}
                            {isModeUpdate(<SubmitButton label="Mettre à jour" onClick={(event) => this.updateDataset(this.state.fields)} disabled={disabled} />)}
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
                <Label htmlFor="dcId" value="Modèle"/>
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

Dataset.PropTypes = {
    onSubmit: React.PropTypes.func.isRequired
}
