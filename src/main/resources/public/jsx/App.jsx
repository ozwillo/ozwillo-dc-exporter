import React from 'react'
import { render } from 'react-dom'
import { I18nextProvider } from 'react-i18next'
import { Router, Route, IndexRoute, browserHistory} from 'react-router'

import Navbar from './Navbar'
import Dashboard from './Dashboard'
import Dataset from './Dataset'
import i18n from './util/i18n'

import 'bootstrap'

const App = React.createClass({
    getInitialState() {
        return {
            csrfToken: '',
            csrfTokenHeaderName: ''
        }
    },
    childContextTypes: {
        csrfToken: React.PropTypes.string,
        csrfTokenHeaderName: React.PropTypes.string,
        t : React.PropTypes.func
    },
    getChildContext() {
        return {
            csrfToken: this.state.csrfToken,
            csrfTokenHeaderName: this.state.csrfTokenHeaderName,
            t: this.t
        };
    },
    componentDidMount() {
        fetch('/api/csrf-token', { credentials: 'same-origin' })
            .then(response => response.headers)
            .then(headers =>
                this.setState({ csrfToken : headers.get('X-CSRF-TOKEN'), csrfTokenHeaderName: headers.get('X-CSRF-HEADER') }))
    },
    render() {
        return (
            <div>
                <Navbar />
                {this.props.children}
            </div>
        )
    }
})

render(
    <I18nextProvider i18n={ i18n }>
        <Router history={browserHistory}>
            <Route path="/" component={App}>
                <IndexRoute component={Dashboard} />
                <Route path="dataset" component={Dataset} />
                <Route path="dataset/:id" component={Dataset}/>
            </Route>
        </Router>
    </I18nextProvider>
, document.getElementById('app'))
