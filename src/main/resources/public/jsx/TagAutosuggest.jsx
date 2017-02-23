import React, {Component} from "react"

import Autosuggest from "react-autosuggest"

const getSuggestionValue = suggestion => suggestion.name

const renderSuggestion = suggestion => (
    <span>{suggestion.name}</span>
)

function escapeRegexCharacters(str) {
    return str.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
}

class TagAutosuggest extends React.Component {
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
        this.onClick = this.onClick.bind(this)
        this.renderInputComponent = this.renderInputComponent.bind(this)
    }
    componentDidMount() {
        fetch('/api/ckan/tags', {credentials: 'same-origin'})
            .then(response => response.json())
            .then(json => this.setState({allSuggestions: json}))
    }
    getSuggestions(value) {
        const escapedValue = escapeRegexCharacters(value.trim());

        if (escapedValue === '') {
            return [];
        }

        const regex = new RegExp('^' + escapedValue, 'i');

        return this.state.allSuggestions.filter(suggestion => regex.test(suggestion.name));
    }
    onChange = (event, { newValue }) => {
        this.setState({ value: newValue })
    }
    onSuggestionsFetchRequested = ({ value }) => {
        this.setState({
            suggestions: this.getSuggestions(value)
        })
    }
    onSuggestionsClearRequested = () => {
        this.setState({ suggestions: [] })
    }
    onSuggestionSelected = (event, { suggestion}) => {
        this.props.onSelect(suggestion)
        this.setState({ value: "" })
    }
    onClick(){

        this.props.onSelect({ name: this.state.value })

        this.setState({ value: "" })

    }
    renderInputComponent = (inputProps) => (
        <div className="input-group">
            <input {...inputProps} className="form-control" />
            <span className="input-group-btn">
                <button className="btn btn-default" type="button" onClick={this.onClick} >Ajouter</button>
            </span>
        </div>
    )
    render() {
        const inputProps = {
            value: this.state.value,
            onChange: this.onChange
        }
        return (
            <div className="col-sm-9">
                <Autosuggest
                    id={"TagAutosuggest"}
                    suggestions={this.state.suggestions}
                    onSuggestionsFetchRequested={this.onSuggestionsFetchRequested}
                    onSuggestionsClearRequested={this.onSuggestionsClearRequested}
                    onSuggestionSelected={this.onSuggestionSelected}
                    getSuggestionValue={getSuggestionValue}
                    renderSuggestion={renderSuggestion}
                    renderInputComponent={this.renderInputComponent}
                    inputProps={inputProps}/>
            </div>
        )
    }
}

TagAutosuggest.PropTypes = {
    onSelect: React.PropTypes.func.isRequired
}

const Tag = ({ keyword, remove, id }) =>
    <li id="tag" className="list-group-item">{keyword}<span className="glyphicon glyphicon-remove remove-tag" onClick={() => remove(id)}></span></li>

module.exports = { TagAutosuggest, Tag }