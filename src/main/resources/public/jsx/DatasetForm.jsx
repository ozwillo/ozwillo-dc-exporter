import React from 'react'
import renderIf from 'render-if'

import DatasetAutosuggest from './DatasetAutosuggest'
import { TagAutosuggest, Tag } from './TagAutosuggest'
import { FormGroup, Label, InputText, SelectField, Textarea } from './Form'

export default class DatasetForm extends React.Component {
    static propTypes = {
        onDatasetNameChange: React.PropTypes.func.isRequired,
        onFieldChange: React.PropTypes.func.isRequired,
        tags: React.PropTypes.array.isRequired,
        source: React.PropTypes.string.isRequired,
        licenses: React.PropTypes.object.isRequired,
        license: React.PropTypes.string.isRequired,
        datasetName: React.PropTypes.string.isRequired
    }
    constructor(props) {
        super(props)
        this.handleDelete = this.handleDelete.bind(this)
        this.handleAddition = this.handleAddition.bind(this)
        this.handleExistingBtn = this.handleExistingBtn.bind(this)
        this.handleNewBtn = this.handleNewBtn.bind(this)
    }
    state = {
        datasetName: '',
        newDataset: false
    }
    handleDelete(i) {
        let tags = this.state.fields.tags
        tags.splice(i, 1)
        this.props.onFieldChange('tags', tags)
    }
    handleAddition(tag) {
        let tags = this.state.fields.tags
        tags.push(tag)
        this.props.onFieldChange('tags', tags)
    }
    handleExistingBtn() {
        this.props.onFieldChange('name', '')
        this.setState({ newDataset: false })
    }
    handleNewBtn() {
        this.props.onFieldChange('name', '')
        this.props.onFieldChange('ckanPackageId', '')
        this.setState({ newDataset: true })
    }
    render() {
        const tags = this.props.tags.map(( tag, key ) =>
            <Tag key={key} keyword={tag.name} remove={this.handleDelete} id={key} />)

        const existingClassName = !this.state.newDataset ? "btn btn-primary" : "btn btn-default"
        const newClassName = this.state.newDataset ? "btn btn-primary" : "btn btn-default"

        const existingDataset = renderIf(this.state.newDataset == false)
        const newDataset = renderIf(this.state.newDataset == true)

        return (
            <div className="panel panel-default">
                <div className="panel-heading">
                    <h3 className="panel-title">Jeu de données</h3>
                </div>
                <div className="panel-body">
                    <div className="center-outer-div">
                        <div className="btn-group center-inner-div">
                            <button type="button" className={existingClassName} onClick={this.handleExistingBtn}>Existant</button>
                            <button type="button" className={newClassName} onClick={this.handleNewBtn}>Nouveau</button>
                        </div>
                    </div>
                    {existingDataset(
                        <FormGroup>
                            <Label htmlFor="autosuggest-name" value="Name"/>
                            <DatasetAutosuggest id="autosuggest-name" initialValue={this.props.datasetName} onSelect={ this.props.onDatasetNameChange }/>
                        </FormGroup>
                    )}
                    {newDataset(
                        <div>
                            <FormGroup>
                                <Label htmlFor="name" value="Name" />
                                <InputText id="name" value={this.props.datasetName}
                                           onChange={(event) => this.props.onFieldChange(event.target.id, event.target.value)}/>
                            </FormGroup>
                            <FormGroup>
                                <Label htmlFor="notes" value="Description" />
                                <Textarea id="notes" value={this.props.notes}
                                           onChange={(event) => this.props.onFieldChange(event.target.id, event.target.value)}/>
                            </FormGroup>
                            <FormGroup>
                                <Label htmlFor="source" value="Source" />
                                <InputText id="source" value={this.props.source}
                                           onChange={(event) => this.props.onFieldChange(event.target.id, event.target.value)}/>
                            </FormGroup>
                            <LicenceChooser licenses={this.props.licenses} currentLicense={this.props.license}
                                            onChange={(event) => this.props.onFieldChange(event.target.id, event.target.value)}/>
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
                        </div>
                    )}
                </div>
            </div>
        )
    }
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
