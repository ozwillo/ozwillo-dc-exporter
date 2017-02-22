import React, {Component} from "react"

import Autosuggest from "react-autosuggest"

const getSuggestionValue = suggestion => suggestion.title

const renderSuggestion = suggestion => (
    <span>{suggestion.title}</span>
)

const renderInputComponent = inputProps => (
    <input {...inputProps} className="form-control"  />
)

function escapeRegexCharacters(str) {
    return str.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
}

export default class DatasetAutosuggest extends React.Component {
    constructor (props) {
        super(props)

        this.defaultProps = {
            required: false
        }

        this.state = {
            value: '',
            suggestions: [],
            allSuggestions: {}

        }
    }
    componentDidMount() {
        fetch('/api/ckan/datasets', {credentials: 'same-origin'})
            .then(response => response.json())
            .then(json => this.setState({allSuggestions: json}))
    }
    getSuggestions(value) {
        const escapedValue = escapeRegexCharacters(value.trim());

        if (escapedValue === '') {
            return [];
        }

        const regex = new RegExp('^' + escapedValue, 'i');

        return this.state.allSuggestions.filter(suggestion => regex.test(suggestion.title));
    }
    onChange = (event, { newValue }) => {
        this.setState({ value: newValue })
        this.props.onSelect(newValue)
    }
    onSuggestionsFetchRequested = ({ value }) => {
        this.setState({
            suggestions: this.getSuggestions(value)
        })
    }
    onSuggestionsClearRequested = () => {
        this.setState({ suggestions: [] })
    }
    onSuggestionSelected = (event, { suggestion }) => {
        this.props.onSelect(suggestion)
    }
    render() {
        const inputProps = {
            value: this.state.value,
            onChange: this.onChange,
            required: true
        }
        return (
            <div className="col-sm-9">
                <Autosuggest
                    id={"DatasetAutosuggest"}
                    suggestions={this.state.suggestions}
                    onSuggestionsFetchRequested={this.onSuggestionsFetchRequested}
                    onSuggestionsClearRequested={this.onSuggestionsClearRequested}
                    onSuggestionSelected={this.onSuggestionSelected}
                    getSuggestionValue={getSuggestionValue}
                    renderSuggestion={renderSuggestion}
                    renderInputComponent={renderInputComponent}
                    inputProps={inputProps}/>
            </div>
        )
    }
}

DatasetAutosuggest.PropTypes = {
    onSelect: React.PropTypes.func.isRequired
}