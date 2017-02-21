import React from 'react'

import { Link, IndexLink } from 'react-router'

export default React.createClass({
    render() {
        return (
            <nav className="navbar navbar-default">
                <div className="container-fluid">
                    <div className="navbar-header">
                        <button type="button" className="navbar-toggle collapsed" data-toggle="collapse"
                                data-target="#navbar-collapse" aria-expanded="false">
                            <span className="sr-only">Toggle navigation</span>
                            <span className="icon-bar"></span>
                            <span className="icon-bar"></span>
                            <span className="icon-bar"></span>
                        </button>
                        <IndexLink to="/" className="navbar-brand">DC Exporter</IndexLink>
                    </div>

                    <div className="collapse navbar-collapse" id="navbar-collapse">
                        <ul className="nav navbar-nav">
                            <li><IndexLink to="/">Flux d'activités</IndexLink></li>
                            <li><Link to="/dataset">Ajouter un jeu de données</Link></li>
                        </ul>
                    </div>
                </div>
            </nav>
        )
    }
})
