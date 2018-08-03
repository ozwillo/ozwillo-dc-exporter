import React from 'react'
import PropTypes from 'prop-types'

const Form = ({ id, onSubmit, children }) =>
    <form className="needs-validation" id={id} onSubmit={onSubmit} noValidate>
        {children}
    </form>

Form.propTypes = {
    id: PropTypes.string.isRequired,
    onSubmit: PropTypes.func.isRequired
}

const Fieldset = ({ legend, children }) =>
    <fieldset className="form-group">
        <legend style={{ color: '#6f438e' }}>{legend}</legend>
        {children}
    </fieldset>

Fieldset.propTypes = {
    legend: PropTypes.string.isRequired
}

const FormGroup = ({ children }) =>
    <div className="form-group row">
        {children}
    </div>

const Label = ({ htmlFor, value}) =>
    <label htmlFor={htmlFor} className="col-sm-3 col-form-label col-form-label-sm">{value}</label>

Label.propTypes = {
    htmlFor: PropTypes.string.isRequired,
    value: PropTypes.string.isRequired
}

const SelectField = ({ id, value, onChange, isMultiple, children }) =>
    <div className="col-sm-9">
        <select name={id} id={id} value={value} className="form-control form-control-sm custom-select" multiple={isMultiple} onChange={onChange}>
            <option key="-1" value=""></option>
            {children}
        </select>
    </div>

SelectField.propTypes = {
    id: PropTypes.string.isRequired,
    value: PropTypes.oneOfType([
        PropTypes.string,
        PropTypes.arrayOf(PropTypes.string)
    ]),
    onChange: PropTypes.func.isRequired,
    isMultiple: PropTypes.bool,
    children: PropTypes.node.isRequired
}

SelectField.defaultProps = {
    isMultiple: false
}
const Input = ({ id, value, type, required, readOnly, onChange }) =>
    <div className="col-sm-9">
        <input readOnly={readOnly} type={type} name={id} id={id} value={value} className="form-control form-control-sm" onChange={onChange}
               required={required} />
    </div>

Input.propTypes = {
    id: PropTypes.string.isRequired,
    value: PropTypes.oneOfType([
        PropTypes.string,
        PropTypes.number
    ]),
    required: PropTypes.bool,
    onChange: PropTypes.func
}

Input.defaultProps = {
    required: true,
    readOnly: false
}

const ReadOnlyField = ({ id, value}) =>
    <div className="col-sm-9">
        <input type="text" readOnly className="form-control-plaintext form-control-sm" id={id} value={value} />
    </div>

ReadOnlyField.propTypes = {
    id: PropTypes.string.isRequired,
    value: PropTypes.string.isRequired
}

const Textarea = ({ id, value, onChange }) =>
    <div className="col-sm-9">
        <textarea name={id} id={id} value={value} className="form-control form-control-sm" onChange={onChange}></textarea>
    </div>

const Checkbox = ({ handleCheckboxChange, label, value, checked }) =>
    <div className="checkbox">
        <label>
            <input type="checkbox" value={value} checked={checked} onChange={(event) => handleCheckboxChange(event)} />
            {label}
        </label>
    </div>
    

const SubmitButton = ({ label, disabled }) =>
    <div className="form-group row">
        <div className="offset-sm-3 col-sm-7">
            <button type="submit" className="btn btn-primary" disabled={disabled}>{label}</button>
        </div>
    </div>

SubmitButton.propTypes = {
    label: PropTypes.string.isRequired,
    disabled: PropTypes.bool
}

SubmitButton.defaultProps = {
    disabled: false
}

const Alert = ({ message, success, closeMethod }) =>
    <div className={'row alert ' + (success ? 'alert-success' : 'alert-danger')} role="alert">
        <div className="col-sm-11 text-center">
            {message}
        </div>
        <div className="col-sm-1">
            <button type="button" className="close" onClick={closeMethod}>
                <span>&times;</span>
            </button>
        </div>
    </div>

export { Form, Fieldset, FormGroup, Label, SelectField, Input, Textarea, SubmitButton, Alert, ReadOnlyField, Checkbox }