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
            {"name":"citizenkin_0"}], tags: [], suggestions: [] }

        this.onDatasetSelected = this.onDatasetSelected.bind(this)
        this.registerDataset = this.registerDataset.bind(this)
        this.handleAddition = this.handleAddition.bind(this)
        this.handleDelete = this.handleDelete.bind(this)
        this.handleDrag = this.handleDrag.bind(this)
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
        console.log(this.state.suggestions);
        const dataset = this.state.datasets.find(function(dataset){
            return dataset['@id'] == dcId
        });
        console.log(dataset)
        this.setState({ type: dataset['dcmo:name']})
        this.setState({ dcId })
    }
    registerDataset(fields) {
        fields['dcId'] = this.state.dcId
        fields['type'] = this.state.type
        fields['project'] = this.state.project
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
    handleDelete(i) {
        let tags = this.state.tags
        tags.splice(i, 1)
        this.setState({tags: tags})
    }
    handleAddition(tag) {
        let tags = this.state.tags
        tags.push({
            id: tags.length + 1,
            text: tag
        })
        this.setState({tags: tags})
    }
    handleDrag(tag, currPos, newPos) {
        let tags = this.state.tags

        // mutate array
        tags.splice(currPos, 1)
        tags.splice(newPos, 0, tag)

        // re-render
        this.setState({ tags: tags })
    }
    render() {
        let tags = this.state.tags
        let suggections = this.state.suggestions
        let tabSuggestions = Object.keys(this.state.suggestions).map(key =>
            suggections[key]
        )
        return (
            <div>
                <h1>Dataset registration</h1>
                <DatasetChooser dcId={this.state.dcId} onDatasetSelected={this.onDatasetSelected}
                    datasets={this.state.datasets} />

                {renderIf(this.state.dcId)(
                    <div>
                        <Form>
                            <FormGroup>
                                <Label htmlFor="Tags" value="Tag"/>
                                <ReactTags tags={tags}
                                           suggestions={tabSuggestions}
                                           handleDelete={this.handleDelete}
                                           handleAddition={this.handleAddition}
                                           handleDrag={this.handleDrag} />
                            </FormGroup>
                        </Form>
                        <DatasetConfigurer onSubmit={this.registerDataset} licenses={this.state.licenses} datasets={this.state.datasets} projects={this.state.projects} />
                    </div>
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

class DatasetConfigurer extends React.Component {
    constructor(props) {
        super(props)

        this.state = {
            fields: {
                name: '',
                type: '',
                license: '',
                project: ''
            }
        }
        this.onFieldChange = this.onFieldChange.bind(this)
    }
    onFieldChange(id, value) {
        const fields = this.state.fields
        fields[id] = value
        this.setState({ fields })
    }
    render() {
        return (
            <Form>
                <FormGroup>
                    <Label htmlFor="name" value="Name" />
                    <InputText id="name" value={this.props.name}
                               onChange={(event) => this.onFieldChange(event.target.id, event.target.value)}/>
                </FormGroup>
                <ProjectChooser project={this.state.project}
                                onClick={(event) => this.props.onSubmit(this.state.fields)} projects={this.props.projects} />
                <LicenceChooser licenses={this.props.licenses} currentLicense={this.state.fields['license']}
                                onChange={(event) => this.onFieldChange(event.target.id, event.target.value)}/>
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
