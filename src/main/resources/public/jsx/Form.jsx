import React from 'react'

const Form = ({ children }) =>
    <form className="form-horizontal">
        {children}
    </form>

const FormGroup = ({ children }) =>
    <div className="form-group">
        {children}
    </div>

const Label = ({ htmlFor, value }) =>
    <label htmlFor={htmlFor} className="control-label col-sm-3">{value}</label>

const SelectField = ({ id, value, onChange, children }) =>
    <div className="col-sm-9">
        <select name={id} id={id} value={value} className="form-control" onChange={onChange}>
            <option key="-1" value=""></option>
            {children}
        </select>
    </div>

const InputText = ({ id, value, onChange }) =>
    <div className="col-sm-9">
        <input type="text" name={id} id={id} value={value} className="form-control" onChange={onChange} />
    </div>

const ReadOnlyField = ({ id, value}) =>
    <div className="col-sm-9 read-only-field">
        <span id={id}>{value}</span>
    </div>

const Textarea = ({ id, value, onChange }) =>
    <div className="col-sm-9">
        <textarea name={id} id={id} value={value} className="form-control" onChange={onChange}></textarea>
    </div>

const SubmitButton = ({ label, onClick, disabled }) =>
    <div className="form-group">
        <div className="col-sm-offset-3 col-sm-7">
            <button type="button" className="btn btn-default" onClick={onClick} disabled={disabled}>{label}</button>
        </div>
    </div>

const Alert = ({ message, success, closeMethod }) =>
    <div className={'row alert ' + (success ? 'alert-success' : 'alert-danger')} role="alert">
        <div className="col-sm-11">
            {message}
        </div>
        <div className="col-sm-1">
            <button type="button" className="close" onClick={closeMethod}>
                <span>&times;</span>
            </button>
        </div>
    </div>

module.exports = { Form, FormGroup, Label, SelectField, InputText, Textarea, SubmitButton, Alert, ReadOnlyField }
