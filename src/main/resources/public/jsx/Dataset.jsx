import React from 'react'
import renderIf from 'render-if'
import { browserHistory } from 'react-router';
import { translate } from 'react-i18next'

import DatasetForm from './DatasetForm'
import GeocodingForm from './GeocodingForm'
import { Form, FormGroup, Label, SelectField, InputText, Textarea, SubmitButton, Alert, ReadOnlyField, Checkbox } from './Form'

class Dataset extends React.Component {
    constructor(props, context) {
        super(props, context)

        this.state = {
            datasets: [],
            datasetsFetched: false,
            dataset: [],
            datasetFetched: false,
            newDataset: false,
            licenses: {},
            organizations:[],
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
                organizationId:'',
                description: '',
                license: '',
                source: '',
                tags: [],
                excludedFields: [],
                addressField: '',
                postalCodeField: '',
                cityField: ''
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
        this.onChangeNotif = this.onChangeNotif.bind(this)
        this.closeNotif = this.closeNotif.bind(this)
        this.selectedCheckboxes = new Set();
    }
    checkStatus(response) {
        if (response.status >= 200 && response.status < 300) {
            return response
        } else {
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
            .then(this.checkStatus)
            .then(response => response.json())
            .then(json => this.setState({licenses: json}))
            .catch(error => {
                error.text().then(text => { this.onChangeNotif(false, text) })
            })
        fetch('/api/ckan/organizations', {credentials: 'same-origin'})
            .then(this.checkStatus)
            .then(response => response.json())
            .then(json => this.setState({organizations: json}))
            .catch(error => {
                error.text().then(text => { this.onChangeNotif(false, text) })
            })
    }
    componentDidUpdate() {
        if(this.props.params.id && this.state.fieldsFetched && this.state.datasetsFetched && !this.state.datasetFetched) {
            fetch('/api/dc/model/' + this.state.fields.project + '/' + this.state.fields.type, { credentials: 'same-origin' })
                .then(response => response.json())
                .then(json => this.setState({dataset: json, datasetFetched: true}))
        }
    }
    loadMapping(id){
        fetch('/api/dc-model-mapping/model/' + id, { credentials: 'same-origin' })
            .then(response => response.json())
            .then(json => {
                this.setState({fields: json, mode: 'update', fieldsFetched: true, newDataset: false})
                json.excludedFields.forEach(elem => this.selectedCheckboxes.add(elem))
            })
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
            this.setState({ message: 'dataset.notif.is_created' })
        })
        .catch(text => this.setState({ success : false, message: text }))
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
        .then(() => this.setState({ message: 'dataset.notif.is_updated' }))
        .catch(response => {
            response.text().then(text => this.setState({ success : false, message: text }))
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
    onChangeNotif(success, message){
        this.setState({ success: success, message: message })
    }
    closeNotif(){
        this.setState({ message: '' })
    }
    render() {
        const { t } = this.context
        const fields = this.state.datasetFetched && this.state.fields.dcId ?
            this.state.dataset['dcmo:globalFields'].map((field, key) =>
                <Checkbox label={field['dcmf:name']} handleCheckboxChange={this.toggleCheckbox} key={key}
                          checked={!this.state.fields.excludedFields.includes(field['dcmf:name'])}/>)
            : null

        const disabled = this.state.fields.name == null || this.state.fields.name == '' ||
                         this.state.fields.resourceName == null || this.state.fields.resourceName == '' ||
                         ((this.state.fields.organizationId == null || this.state.fields.organizationId == '') && this.state.newDataset)

        const isModeCreate = renderIf(this.state.mode == 'create')
        const isModeUpdate = renderIf(this.state.mode == 'update')

        return (
            <div  id="container" className="container">
                <h1>{ t('dataset.title') }</h1>
                {renderIf(this.state.message)(
                    <Alert message={ t(this.state.message) } success={this.state.success} closeMethod={this.closeNotif}/>
                )}
                <Form>
                    <div className="panel panel-default">
                        <div className="panel-heading">
                            <h3 className="panel-title">{ t('dataset.panel.datacore') }</h3>
                        </div>
                        <div className="panel-body">
                            {isModeCreate(
                                <DatasetChooser dcId={this.state.fields.dcId} onDatasetSelected={this.onDatasetSelected}
                                    datasets={this.state.datasets} t={t} />
                            )}
                            {isModeUpdate(
                                <FormGroup>
                                    <Label htmlFor="model" value={ t('dataset.label.model') } />
                                    <ReadOnlyField id="model" value={this.state.fields.type} />
                                </FormGroup>
                            )}
                            {renderIf(this.state.fields.dcId)(
                                <div>
                                    <FormGroup>
                                        <Label htmlFor="version" value={ t('dataset.label.version') } />
                                        {isModeCreate(<InputText id="version" value={this.state.fields.version}/>)}
                                        {isModeUpdate(<ReadOnlyField id="version" value={this.state.fields.version}/>)}
                                    </FormGroup>
                                    <FormGroup>
                                        <Label htmlFor="excludedFields" value={ t('dataset.label.export_fields')} />
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
                            {renderIf(this.state.datasetFetched)(
                                <GeocodingForm onFieldChange={this.onFieldChange}
                                               globalFields={this.state.dataset['dcmo:globalFields']}
                                               addressField={this.state.fields.addressField}
                                               postalCodeField={this.state.fields.postalCodeField}
                                               cityField={this.state.fields.cityField} />
                            )}

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
                                         tags={this.state.fields.tags}
                                         organizations={this.state.organizations}
                                         organizationId={this.state.fields['organizationId']}
                                         onChangeNotif={this.onChangeNotif} />

                            <div className="panel panel-default">
                                <div className="panel-heading">
                                    <h3 className="panel-title">{ t('dataset.panel.resource') }</h3>
                                </div>
                                <div className="panel-body">
                                    <FormGroup>
                                        <Label htmlFor="resourceName" value={ t('dataset.label.resource_name') } />
                                        <InputText id="resourceName" value={this.state.fields.resourceName}
                                                   onChange={(event) => this.onFieldChange(event.target.id, event.target.value)}/>
                                    </FormGroup>
                                    <FormGroup>
                                        <Label htmlFor="description" value={ t('dataset.label.description') } />
                                        <Textarea id="description" value={this.state.fields.description}
                                                  onChange={(event) => this.onFieldChange(event.target.id, event.target.value)}/>
                                    </FormGroup>
                                </div>
                            </div>
                            {isModeCreate(<SubmitButton label={ t('action.create') } onClick={(event) => this.registerDataset(this.state.fields)} disabled={disabled} />)}
                            {isModeUpdate(<SubmitButton label={ t('action.update') } onClick={(event) => this.updateDataset(this.state.fields)} disabled={disabled} />)}
                        </div>
                    )}
                </Form>
            </div>
        )
    }
}

Dataset.contextTypes = {
    csrfToken: React.PropTypes.string,
    csrfTokenHeaderName: React.PropTypes.string,
    t: React.PropTypes.func
}

const DatasetChooser = ({ datasets, dcId, onDatasetSelected, t }) => {
    const options = datasets.map(dataset =>
        <option key={dataset['@id']} value={dataset['@id']}>{dataset['dcmo:name']}</option>
    )
    return (
            <FormGroup>
                <Label htmlFor="dcId" value={ t('dataset.label.model') }/>
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
    datasets: React.PropTypes.array.isRequired,
    t: React.PropTypes.func.isRequired
}

Dataset.PropTypes = {
    onSubmit: React.PropTypes.func.isRequired
}

export default translate(['dc-exporter'])(Dataset)