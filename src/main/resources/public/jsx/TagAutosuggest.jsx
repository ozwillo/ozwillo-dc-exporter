import React, {Component} from "react"
import PropTypes from 'prop-types'
import Autosuggest from "react-autosuggest"

const getSuggestionValue = suggestion => suggestion.name

const renderSuggestion = suggestion => (
    <span>{suggestion.name}</span>
)

function escapeRegexCharacters(str) {
    return str.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
}

class TagAutosuggest extends Component {
    constructor (props, context) {
        super(props, context)
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
    checkStatus(response) {
        if (response.status >= 200 && response.status < 300) {
            return response
        } else {
            throw response
        }
    }
    componentDidMount() {
        fetch('/api/ckan/tags', {credentials: 'same-origin'})
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
    renderInputComponent(inputProps) {
        const { t } = this.context
        return (
            <div className="input-group">
                <input {...inputProps} className="form-control"/>
                <span className="input-group-btn">
                    <button className="btn btn-default" type="button" onClick={this.onClick}>{ t('action.add') }</button>
                </span>
            </div>
        )
    }
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

TagAutosuggest.propTypes = {
    onSelect: PropTypes.func.isRequired
}

TagAutosuggest.contextTypes = {
    t: PropTypes.func
}


const Tag = ({ keyword, remove, id }) =>
    <li id="tag" className="list-group-item">{keyword}<span className="glyphicon glyphicon-remove remove-tag" onClick={() => remove(id)}></span></li>

module.exports = { TagAutosuggest, Tag }
