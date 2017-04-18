import React from 'react'
import renderIf from 'render-if'
import { translate } from 'react-i18next'

import DatasetAutosuggest from './DatasetAutosuggest'
import { TagAutosuggest, Tag } from './TagAutosuggest'
import { FormGroup, Label, InputText, SelectField, Textarea } from './Form'

class DatasetForm extends React.Component {
    static propTypes = {
        onChange: React.PropTypes.func.onChange,
        onDatasetNameChange: React.PropTypes.func.isRequired,
        onFieldChange: React.PropTypes.func.isRequired,
        toggleNewDataset: React.PropTypes.func.isRequired,
        newDataset: React.PropTypes.bool.isRequired,
        tags: React.PropTypes.array.isRequired,
        source: React.PropTypes.string.isRequired,
        notes: React.PropTypes.string.isRequired,
        licenses: React.PropTypes.object.isRequired,
        license: React.PropTypes.string.isRequired,
        datasetName: React.PropTypes.string.isRequired,
        onChangeNotif: React.PropTypes.func.isRequired
    }
    static contextTypes = {
        t: React.PropTypes.func
    }
    constructor(props, context) {
        super(props, context)
        this.handleDelete = this.handleDelete.bind(this)
        this.handleAddition = this.handleAddition.bind(this)
    }
    handleDelete(i) {
        let tags = this.props.tags
        tags.splice(i, 1)
        this.props.onFieldChange('tags', tags)
    }
    handleAddition(tag) {
        let tags = this.props.tags
        tags.push(tag)
        this.props.onFieldChange('tags', tags)
    }
    render() {
        const { t } = this.context
        const tags = this.props.tags.map(( tag, key ) =>
            <Tag key={key} keyword={tag.name} remove={this.handleDelete} id={key} />)

        const existingClassName = !this.props.newDataset ? "btn btn-primary" : "btn btn-default"
        const newClassName = this.props.newDataset ? "btn btn-primary" : "btn btn-default"

        const existingDataset = renderIf(this.props.newDataset == false)
        const newDataset = renderIf(this.props.newDataset == true)

        return (
            <div className="panel panel-default">
                <div className="panel-heading">
                    <h3 className="panel-title">{ t('dataset.panel.dataset') } </h3>
                </div>
                <div className="panel-body">
                    <div className="center-outer-div">
                        <div className="btn-group center-inner-div">
                            <button type="button" className={existingClassName} onClick={this.props.toggleNewDataset}>{ t('action.existing') }</button>
                            <button type="button" className={newClassName} onClick={this.props.toggleNewDataset}>{ t('action.new') }</button>
                        </div>
                    </div>
                    {existingDataset(
                        <FormGroup>
                            <Label htmlFor="autosuggest-name" value={t('dataset.label.name')} />
                            <DatasetAutosuggest id="autosuggest-name"
                                                datasetName={this.props.datasetName}
                                                onChange={this.props.onChange}
                                                onChangeNotif={this.props.onChange}
                                                onSelect={this.props.onDatasetNameChange}/>
                        </FormGroup>
                    )}
                    {newDataset(
                        <div>
                            <FormGroup>
                                <Label htmlFor="name" value={t('dataset.label.name')} />
                                <InputText id="name" value={this.props.datasetName}
                                           onChange={(event) => this.props.onFieldChange(event.target.id, event.target.value)}/>
                            </FormGroup>
                            <FormGroup>
                                <Label htmlFor="notes" value={t('dataset.label.description')} />
                                <Textarea id="notes" value={this.props.notes}
                                           onChange={(event) => this.props.onFieldChange(event.target.id, event.target.value)}/>
                            </FormGroup>
                            <FormGroup>
                                <Label htmlFor="source" value={t('dataset.label.source')} />
                                <InputText id="source" value={this.props.source}
                                           onChange={(event) => this.props.onFieldChange(event.target.id, event.target.value)}/>
                            </FormGroup>
                            <LicenceChooser licenses={this.props.licenses} currentLicense={this.props.license}
                                            onChange={(event) => this.props.onFieldChange(event.target.id, event.target.value)}
                                            t={ t }/>
                            <FormGroup>
                                <Label htmlFor="tags" value={ t('dataset.label.tags') }/>
                                <TagAutosuggest onSelect={ this.handleAddition }/>
                                {renderIf(tags.length > 0) (
                                        <div className="col-sm-9 col-sm-offset-3">
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

const LicenceChooser = ({ licenses, currentLicense, onChange, t }) => {
    const options = Object.keys(licenses).map(key =>
        <option key={key} value={key}>{licenses[key]}</option>
    )
    return (
        <FormGroup>
            <Label htmlForm="license" value={ t('dataset.label.license') }/>
            <SelectField id="license" value={currentLicense} onChange={onChange}>
                {options}
            </SelectField>
        </FormGroup>
    )
}

export default translate(['dc-exporter'])(DatasetForm)