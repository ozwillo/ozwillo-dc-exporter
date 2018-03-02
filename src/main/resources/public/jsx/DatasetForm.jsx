import React from 'react'
import renderIf from 'render-if'
import { translate } from 'react-i18next'

import DatasetAutosuggest from './DatasetAutosuggest'
import { TagAutosuggest, Tag } from './TagAutosuggest'
import AddressAutosuggest from './AddressAutosuggest'
import { FormGroup, Label, Input, SelectField, Textarea, Fieldset } from './Form'

class DatasetForm extends React.Component {
    static propTypes = {
        onDatasetNameChange: React.PropTypes.func.isRequired,
        onFieldChange: React.PropTypes.func.isRequired,
        toggleNewDataset: React.PropTypes.func.isRequired,
        newDataset: React.PropTypes.bool.isRequired,
        tags: React.PropTypes.array.isRequired,
        source: React.PropTypes.string.isRequired,
        notes: React.PropTypes.string.isRequired,
        licenses: React.PropTypes.object.isRequired,
        license: React.PropTypes.string.isRequired,
        organizationId:React.PropTypes.string,
        organizations:React.PropTypes.array.isRequired,
        datasetName: React.PropTypes.string.isRequired,
        onChangeNotif: React.PropTypes.func.isRequired,
        geoLocation: React.PropTypes.object.isRequired
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

        const existingClassName = !this.props.newDataset ? "btn btn-primary" : "btn btn-outline-primary"
        const newClassName = this.props.newDataset ? "btn btn-primary" : "btn btn-outline-primary"

        const existingDataset = renderIf(this.props.newDataset == false)
        const newDataset = renderIf(this.props.newDataset == true)

        return (
            <Fieldset legend={t('dataset.panel.dataset')}>
                    <div className="btn-group d-flex justify-content-center mb-sm-3">
                        <button type="button" className={existingClassName} onClick={this.props.toggleNewDataset}>{ t('action.existing') }</button>
                        <button type="button" className={newClassName} onClick={this.props.toggleNewDataset}>{ t('action.new') }</button>
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
                                <Input
                                    id="name"
                                    Type="text"
                                    value={this.props.datasetName}
                                    onChange={(event) => this.props.onFieldChange(event.target.id, event.target.value)}/>
                            </FormGroup>
                            <FormGroup>
                                <Label htmlFor="geo-dataset" value={t('dataset.label.geo_location')} />
                                <AddressAutosuggest id="geo-dataset"
                                                    geoLocation={this.props.geoLocation}
                                                    onFieldChange={this.props.onFieldChange} />
                            </FormGroup>
                            <FormGroup>
                                <Label htmlFor="notes" value={t('dataset.label.description')} />
                                <Textarea id="notes" value={this.props.notes}
                                           onChange={(event) => this.props.onFieldChange(event.target.id, event.target.value)}/>
                            </FormGroup>
                            <FormGroup>
                                <Label htmlFor="source" value={t('dataset.label.source')} />
                                <Input
                                    id="source"
                                    type="text"
                                    value={this.props.source}
                                    onChange={(event) => this.props.onFieldChange(event.target.id, event.target.value)}/>
                            </FormGroup>
                            <LicenceChooser licenses={this.props.licenses} currentLicense={this.props.license}
                                            onChange={(event) => this.props.onFieldChange(event.target.id, event.target.value)}
                                            t={ t }/>
                            <OrganizationChooser organizations={this.props.organizations} currentOrganizationId={this.props.organizationId}
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
            </Fieldset>
        )
    }
}

const LicenceChooser = ({ licenses, currentLicense, onChange, t }) => {
    const options = Object.keys(licenses).map(key =>
        <option key={key} value={key}>{licenses[key]}</option>
    )
    return (
        <FormGroup>
            <Label htmlFor="license" value={ t('dataset.label.license') }/>
            <SelectField id="license" value={currentLicense} onChange={onChange}>
                {options}
            </SelectField>
        </FormGroup>
    )
}

const OrganizationChooser = ({ organizations, currentOrganizationId, onChange, t }) => {
    const options = Object.keys(organizations).
        filter(key => organizations[key].display_name != '')
        .map(key =>
            <option key={key} value={organizations[key].name}>{organizations[key].display_name}</option>
        )
    return (
        <FormGroup>
            <Label htmlFor="organizationId" value={ t('dataset.label.organization')} />
            <SelectField id="organizationId" value={currentOrganizationId} onChange={onChange}>
                {options}
            </SelectField>
        </FormGroup>
    )
}

export default translate(['dc-exporter'])(DatasetForm)