import React, {Component} from "react"
import PropTypes from 'prop-types'
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

class DatasetAutosuggest extends React.Component {
    static defaultProps = {
        required: false
    }
    state = {
        value: '',
        suggestions: [],
        allSuggestions: {},
        isSet: false
    }
    checkStatus(response) {
        if (response.status >= 200 && response.status < 300) {
            return response
        } else {
            throw response
        }
    }
    componentDidMount() {
        fetch('/api/ckan/datasets', {credentials: 'same-origin'})
            .then(this.checkStatus)
            .then(response => response.json())
            .then(json => this.setState({allSuggestions: json}))
            .catch(error => {
                error.text().then(text => { this.props.onChangeNotif(false, text) })
            })
    }
    getSuggestions(value) {
        const escapedValue = escapeRegexCharacters(value.trim());

        if (escapedValue === '') {
            return [];
        }

        const regex = new RegExp(escapedValue, 'i');

        return this.state.allSuggestions.filter(suggestion => regex.test(suggestion.title));
    }
    onChange = (event, { newValue }) => {
        this.setState({ value: newValue })
        this.props.onChange('name', newValue)
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
            value: this.props.datasetName,
            onChange: this.onChange,
            required: true
        }
        return (
            <div className="col-sm-9">
                <Autosuggest
                    id={this.props.id}
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

DatasetAutosuggest.propTypes = {
    id: PropTypes.string.isRequired,
    onChangeNotif: PropTypes.func.isRequired,
    onSelect: PropTypes.func.isRequired
}

export default DatasetAutosuggest
