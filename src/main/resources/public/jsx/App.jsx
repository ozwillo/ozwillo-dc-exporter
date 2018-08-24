import React from 'react'
import PropTypes from 'prop-types'
import { render } from 'react-dom'
import { I18nextProvider } from 'react-i18next'
import { BrowserRouter as Router, Route, IndexRoute/*, browserHistory*/} from 'react-router-dom'
var createReactClass = require('create-react-class');
import Navbar from './Navbar'
import Dashboard from './Dashboard'
import Dataset from './Dataset'
import i18n from './util/i18n'

import 'bootstrap'

class App extends React.Component {
    
    constructor(props, context) {
        super(props, context)
        this.state = {
            csrfToken: '',
            csrfTokenHeaderName: ''
        }
    }
    
    getChildContext() {
        return {
            csrfToken: this.state.csrfToken,
            csrfTokenHeaderName: this.state.csrfTokenHeaderName,
            t: this.t
        };
    }
    componentDidMount() {
        fetch('/api/csrf-token', { credentials: 'same-origin' })
            .then(response => response.headers)
            .then(headers =>
                this.setState({ csrfToken : headers.get('X-CSRF-TOKEN'), csrfTokenHeaderName: headers.get('X-CSRF-HEADER') }))
    }
    render() {
        return (
            <div>
                <Navbar />
                {this.props.children}
            </div>
        )
    }
}

App.childContextTypes = {
    csrfToken: PropTypes.string,
    csrfTokenHeaderName: PropTypes.string,
    t : PropTypes.func
}

render(
    <I18nextProvider i18n={ i18n }>
        <Router /*history={browserHistory}*/>
            <App>
            {/*<Route path="/" component={App}>*/}
                <Route path="/" component={Dashboard} />
                <Route path="dataset" component={Dataset} />
                <Route path="dataset/:id" component={Dataset}/>
            {/*</Route>*/}
            </App>
        </Router>
    </I18nextProvider>
, document.getElementById('app'))
