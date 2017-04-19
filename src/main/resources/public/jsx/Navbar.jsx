import React from 'react'
import { translate } from 'react-i18next'

import { Link, IndexLink } from 'react-router'

class Navbar extends React.Component{
    static contextTypes = {
        t: React.PropTypes.func
    }
    constructor(context){
        super(context)
    }
    render() {
        const { t } = this.context
        return (
            <nav className="navbar navbar-default">
                <div className="container">
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
                            <li><IndexLink to="/">{ t('menu.dashboard') }</IndexLink></li>
                            <li><Link to="/dataset">{ t('menu.add_dataset') }</Link></li>
                        </ul>
                    </div>
                </div>
            </nav>
        )
    }
}
export default translate(['dc-exporter'])(Navbar)
