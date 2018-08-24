import React from 'react'
import PropTypes from 'prop-types'
import { translate } from 'react-i18next'
import { Link, IndexLink } from 'react-router-dom'

import logo from '../img/logo-ozwillo.png'

class Navbar extends React.Component{
    
    constructor(context){
        super(context)
    }
    render() {
        const { t } = this.context
        return (
            <nav className="navbar navbar-expand-lg navbar-light" style={{ backgroundColor: '#4c2d62' }}>
                
                <Link to="/" className="navbar-brand">
                    <img src={logo} width="30" height="30" className="d-inline-block align-top" alt="logo_ozwillo" />
                    <span className="ml-sm-1" style={{ color: '#fff' }}>DC Exporter</span>
                </Link>
                <button className="navbar-toggler" type="button" data-toggle="collapse"
                        data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent"
                        aria-expanded="false" aria-label="Toggle navigation">
                    <span className="navbar-toggler-icon"></span>
                </button>
                <div className="collapse navbar-collapse" id="navbarSupportedContent">
                    <ul className="navbar-nav mr-auto">
                        <li className="nav-item active">
                            <Link className="nav-link" to="/" style={{ color: '#fff' }}>{t('menu.dashboard')} <span className="sr-only">(current)</span></Link>
                        </li>
                        <li className="nav-item">
                            <Link className="nav-link" to="/dataset" style={{ color: '#fff' }}>{t('menu.add_dataset')}</Link>
                        </li>
                    </ul>
                </div>
            </nav>
        )
    }
}

Navbar.contextTypes = {
    t: PropTypes.func
}
export default translate(['dc-exporter'])(Navbar)
