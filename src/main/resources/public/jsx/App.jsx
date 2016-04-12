import React from 'react'
import { render } from 'react-dom'

import { Router, Route, IndexRoute, Link, hashHistory } from 'react-router'

import Navbar from './Navbar'
import Dashboard from './Dashboard'
import DatasetAdder from './DatasetAdder'

const App = React.createClass({
    render() {
        return (
            <div>
                <Navbar />
                {this.props.children}
            </div>
        )
    }
})

render((
    <Router history={hashHistory}>
        <Route path="/" component={App}>
            <IndexRoute component={Dashboard} />
            <Route path="create-dataset" component={DatasetAdder} />
        </Route>
    </Router>
), document.getElementById('app'))
