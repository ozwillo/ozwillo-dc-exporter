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
    <label htmlFor={htmlFor} className="control-label col-sm-2">{value}</label>

const SelectField = ({ id, value, onChange, children }) =>
    <div className="col-sm-8">
        <select name={id} id={id} value={value} className="form-control" onChange={onChange}>
            <option key="-1" value=""></option>
            {children}
        </select>
    </div>

const InputText = ({ id, value, onChange }) =>
    <div className="col-sm-8">
        <input type="text " name={id} id={id} value={value} className="form-control" onChange={onChange} />
    </div>

const SubmitButton = ({ label, onClick }) =>
    <div className="form-group">
        <div className="col-sm-offset-2 col-sm-8">
            <button type="button" className="btn btn-default" onClick={onClick}>{label}</button>
        </div>
    </div>

module.exports = { Form, FormGroup, Label, SelectField, InputText, SubmitButton }
