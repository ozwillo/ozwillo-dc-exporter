import React from "react"

import Autosuggest from "react-autosuggest"

const getSuggestionValue = suggestion => suggestion.display_name

const renderSuggestion = suggestion => (
    <span>{suggestion.display_name}</span>
)

const renderInputComponent = inputProps => (
    <input {...inputProps} className="form-control" />
)

export default class AddressAutosuggest extends React.Component {
    static propTypes = {
        id: React.PropTypes.string.isRequired,
        geoLocation: React.PropTypes.object.isRequired,
        onFieldChange: React.PropTypes.func.isRequired
    }
    state = {
        value: '',
        suggestions: []
    }
    onChange = (event, { newValue }) => {
        this.setState({ value: newValue })
    }
    onSuggestionsFetchRequested = ({ value }) => {
        fetch('https://nominatim.openstreetmap.org/search?q=' + value + ',france&format=json&polygon_json=1', {credentials: 'same-origin'})
            .then(response => response.json())
            .then(json => {
                this.setState({ suggestions: json.filter(suggestion => suggestion.type == 'administrative') })
            })
    }
    onSuggestionsClearRequested = () => {
        this.setState({ suggestions: [] })
    }
    onSuggestionSelected = (event, { suggestion }) => {
        this.setState({ value: suggestion.display_name })
        this.props.onFieldChange( 'geoLocation', {"type": "Point", "coordinates": [suggestion.lon, suggestion.lat]} )
    }
    render() {
        const inputProps = {
            value: this.state.value,
            onChange: this.onChange,
            required: false
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