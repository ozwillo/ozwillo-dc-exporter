import React from 'react'

import { Form, FormGroup } from './Form'

export default React.createClass({
    render() {
        return (
            <div className="container">
                <div className="row">
                    <div className="col-md-12">
                        <h1>Add Dataset</h1>
                    </div>
                    <div className="col-md-12">
                        <DatasetChooser />
                    </div>
                </div>
            </div>
        )
    }
})

const DatasetChooser = React.createClass({
    getInitialState() {
        return {
            datasets: []
        }
    },
    componentDidMount() {
        fetch('/api/dc/models', { credentials: 'same-origin' })
            .then(response => response.json())
            .then(json => this.setState({ datasets: json }))
    },
    render() {
        const options = this.state.datasets.map(dataset =>
            <option key={dataset['@id']} name="dataset" value={dataset['@id']}>{dataset['dcmo:name']}</option>
        )
        return (
            <Form>
                <FormGroup>
                    <label htmlFor="dataset" className="control-label">Choose a dataset</label>
                    <select name="dataset" id="dataset" className="form-control">
                        {options}
                    </select>
                </FormGroup>    
            </Form>
        )
    }
})
