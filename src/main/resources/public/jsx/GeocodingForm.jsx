import React from 'react'
import PropTypes from 'prop-types'
import { translate } from 'react-i18next'

import { FormGroup, Label, SelectField, Fieldset } from './Form'

class GeocodingForm extends React.Component {
    
    constructor(props, context) {
        super(props, context)
    }
    render() {
        const { t } = this.context

        const options = this.props.globalFields.map((field, key) =>
            <option key={key} value={field['dcmf:name']}>{field['dcmf:name']}</option>
        )

        return (
            <Fieldset legend={t('dataset.panel.geocoding')}>
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
            </Fieldset>
        )
    }
}

GeocodingForm.propTypes = {
    onFieldChange: PropTypes.func.isRequired,
    globalFields: PropTypes.array.isRequired,
    addressField: PropTypes.string.isRequired,
    postalCodeField: PropTypes.string.isRequired,
    cityField: PropTypes.string.isRequired
}
GeocodingForm.contextTypes = {
    t: PropTypes.func
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
    id: PropTypes.string.isRequired,
    fieldName: PropTypes.string.isRequired,
    fieldContent: PropTypes.string.isRequired,
    onFieldChange: PropTypes.func.isRequired,
    options: PropTypes.array.isRequired,
    t: PropTypes.func.isRequired
}

export default translate(['dc-exporter'])(GeocodingForm)