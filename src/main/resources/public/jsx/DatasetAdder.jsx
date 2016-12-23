import React from 'react'

import renderIf from 'render-if'

import { WithContext as ReactTags } from 'react-tag-input'

import tagStyles from '../reactTags.css'

import { Form, FormGroup, Label, SelectField, InputText, SubmitButton } from './Form'

export default class DatasetAdder extends React.Component {
    constructor(props) {
        super(props)

        this.state = { dcId: '', type: '', datasets: [], licenses: {}, projects:[{"name":"oasis.sandbox"},
            {"name":"oasis.main"},
            {"name":"oasis.meta"},
            {"name":"geo"},
            {"name":"geo_1"},
            {"name":"org"},
            {"name":"org_1"},
            {"name":"oasis.sample"},
            {"name":"samples_org1"},
            {"name":"samples_org2"},
            {"name":"samples_org3"},
            {"name":"citizenkin"},
            {"name":"citizenkin_0"}], suggestions: [], version: ''}

        this.onDatasetSelected = this.onDatasetSelected.bind(this)
        this.registerDataset = this.registerDataset.bind(this)
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
        this.setState({ type: dataset['dcmo:name']})
        this.setState({ version: dataset['o:version']})
        this.setState({ dcId })
    }
    registerDataset(fields) {
        fields['dcId'] = this.state.dcId
        fields['type'] = this.state.type
        fields['project'] = this.state.project
        fields['tags'] = ''
        console.log(JSON.stringify(fields))
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
    }
    render() {
        return (
            <div>
                <h1>Dataset registration</h1>
                <DatasetChooser dcId={this.state.dcId} onDatasetSelected={this.onDatasetSelected}
                    datasets={this.state.datasets} />
                <Version version={this.state.version} />
                {renderIf(this.state.dcId)(
                        <DatasetConfigurer onSubmit={this.registerDataset} licenses={this.state.licenses}
                                           datasets={this.state.datasets} projects={this.state.projects}
                                           suggestions={this.state.suggestions} />
                )}
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
        <Form>
            <FormGroup>
                <Label htmlFor="dcId" value="Choose a dataset"/>
                <SelectField id="dcId" value={dcId}
                             onChange={(event) => onDatasetSelected(event.target.value)}>
                    {options}
                </SelectField>
            </FormGroup>
        </Form>
    )
}

DatasetChooser.propTypes = {
    dcId: React.PropTypes.string.isRequired,
    onDatasetSelected: React.PropTypes.func.isRequired,
    datasets: React.PropTypes.array.isRequired
}

const Version = ({ version }) => {
    return (
        <Form>
            <FormGroup>
                <Label htmlFor="version" value="Version" />
                <InputText id="version" value={version}/>
            </FormGroup>
        </Form>
    )
}

class DatasetConfigurer extends React.Component {
    constructor(props) {
        super(props)

        this.state = {
            fields: {
                packageName: '',
                name: '',
                description: '',
                license: '',
                project: '',
                source: '',
                tags: []
            }
        }
        this.onFieldChange = this.onFieldChange.bind(this)
        this.handleAddition = this.handleAddition.bind(this)
        this.handleDelete = this.handleDelete.bind(this)
        this.handleDrag = this.handleDrag.bind(this)
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
    handleDrag(tag, currPos, newPos) {
        let tags = this.state.fields['tags']

        // mutate array
        tags.splice(currPos, 1)
        tags.splice(newPos, 0, tag)

        // re-render
        this.onFieldChange('tags', tags)
    }
    render() {
        return (
            <Form>
                <FormGroup>
                    <Label htmlFor="packageName" value="Package Name" />
                    <InputText id="packageName" value={this.state.packageName}
                               onChange={(event) => this.onFieldChange(event.target.id, event.target.value)}/>
                </FormGroup>
                <FormGroup>
                    <Label htmlFor="name" value="Name" />
                    <InputText id="name" value={this.state.name}
                               onChange={(event) => this.onFieldChange(event.target.id, event.target.value)}/>
                </FormGroup>
                <FormGroup>
                    <Label htmlFor="description" value="Description" />
                    <InputText id="description" value={this.state.description}
                               onChange={(event) => this.onFieldChange(event.target.id, event.target.value)}/>
                </FormGroup>
                <FormGroup>
                    <Label htmlFor="source" value="Source" />
                    <InputText id="source" value={this.state.source}
                               onChange={(event) => this.onFieldChange(event.target.id, event.target.value)}/>
                </FormGroup>
                <ProjectChooser currentProject={this.state.project}
                                onChange={(event) => this.onFieldChange(event.target.id, event.target.value)} projects={this.props.projects} />
                <LicenceChooser licenses={this.props.licenses} currentLicense={this.state.fields['license']}
                                onChange={(event) => this.onFieldChange(event.target.id, event.target.value)}/>
                <TagAutocomplet tags={this.state.fields['tags']} suggestions={this.props.suggestions}
                                handleAddition={this.handleAddition} handleDelete={this.handleDelete} />
                <SubmitButton label="Create" onClick={(event) => this.props.onSubmit(this.state.fields)} />
            </Form>
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

const ProjectChooser = ({ projects, currentProject, onChange}) => {
    const options = projects.map(project =>
        <option key={project['name']} value={project['name']}>{project['name']}</option>
    )
    return (
            <FormGroup>
                <Label htmlFor="project" value="Choose a project"/>
                <SelectField id="project" value={currentProject} onChange={onChange}>
                    {options}
                </SelectField>
            </FormGroup>
    )
}

const TagAutocomplet = ({ tags, suggestions, handleDelete, handleAddition}) => {
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
