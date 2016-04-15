import React from 'react'

const Form = ({ children }) =>
    <form className="form-horizontal">
        {children}
    </form>

const FormGroup = ({ children}) =>
    <div className="form-group">
        {children}
    </div>

module.exports = { Form, FormGroup }
