import React from 'react'
import { translate } from 'react-i18next'

import { FormGroup, Label, SelectField } from './Form'

class GeocodingForm extends React.Component {
    static propTypes = {
        onFieldChange: React.PropTypes.func.isRequired,
        globalFields: React.PropTypes.array.isRequired,
        addressField: React.PropTypes.string.isRequired,
        postalCodeField: React.PropTypes.string.isRequired,
        cityField: React.PropTypes.string.isRequired
    }
    static contextTypes = {
        t: React.PropTypes.func
    }
    constructor(props, context) {
        super(props, context)
    }
    render() {
        const { t } = this.context

        const options = this.props.globalFields.map((field, key) =>
            <option key={key} value={field['dcmf:name']}>{field['dcmf:name']}</option>
        )

        return (
            <div className="panel panel-default">
                <div className="panel-heading">
                    <h3 className="panel-title">{ t('dataset.panel.geocoding') } </h3>
                </div>
                <div className="panel-body">
                    <FieldChooser   id="addressField"
                                    fieldName="address_field"
                                    fieldContent={this.props.addressField}
                                    onFieldChange={(event) => this.props.onFieldChange(event.target.id, event.target.value)}
                                    options={ options } t={t} />

                    <FieldChooser   id="cityField"
                                    fieldName="city_field"
                                    fieldContent={this.props.cityField}
                                    onFieldChange={(event) => this.props.onFieldChange(event.target.id, event.target.value)}
                                    options={ options } t={t} />

                    <FieldChooser   id="postalCodeField"
                                    fieldName="postal_code_field"
                                    fieldContent={this.props.postalCodeField}
                                    onFieldChange={(event) => this.props.onFieldChange(event.target.id, event.target.value)}
                                    options={ options } t={t} />
                </div>
            </div>
        )
    }
}

const FieldChooser = ({ id, fieldName, options, fieldContent, onFieldChange, t }) => {
    return (
        <FormGroup>
            <Label htmlFor={ fieldName } value={ t('dataset.label.' + fieldName)} />
            <SelectField id={ id } value={ fieldContent } onChange={ onFieldChange }>
                { options }
            </SelectField>
        </FormGroup>
    )
}

FieldChooser.propTypes = {
    id: React.PropTypes.string.isRequired,
    fieldName: React.PropTypes.string.isRequired,
    fieldContent: React.PropTypes.string.isRequired,
    onFieldChange: React.PropTypes.func.isRequired,
    options: React.PropTypes.array.isRequired,
    t: React.PropTypes.func.isRequired
}

export default translate(['dc-exporter'])(GeocodingForm)