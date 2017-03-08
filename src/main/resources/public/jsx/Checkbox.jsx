import React, { Component, PropTypes } from 'react';

class Checkbox extends Component {
    static propTypes = {
        isChecked: React.PropTypes.bool.isRequired
    }

    state = {
        isChecked: true,
    }

    toggleCheckboxChange = () => {
        const { handleCheckboxChange, label } = this.props;

        this.setState(({ isChecked }) => (
            {
                isChecked: !isChecked,
            }
        ));

        handleCheckboxChange(label);
    }

    componentDidMount() {
        this.setState({isChecked: this.props.checked})
    }

    render() {
        const { label } = this.props;
        const { isChecked } = this.state;

        return (
            <div className="checkbox">
                <label>
                    <input
                        type="checkbox"
                        value={label}
                        checked={isChecked}
                        onChange={this.toggleCheckboxChange}
                    />

                    {label}
                </label>
            </div>
        );
    }
}

Checkbox.propTypes = {
    label: PropTypes.string.isRequired,
    handleCheckboxChange: PropTypes.func.isRequired,
};

export default Checkbox;
