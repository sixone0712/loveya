import React, { Component } from "react";
import {
  Card,
  CardBody,
  Col,
  FormGroup,
  ButtonToggle,
  Button,
  Input
} from "reactstrap";
import Checkbox from "./checkbox";
import ReactTransitionGroup from "react-addons-css-transition-group";

const formGroupStyle = {
  marginBottom: "0px",
  fontSize: "15px"
};

const buttonPosition = {
  position: "absolute",
  top: "17px",
  right: "20px"
};

const selectStyle = {
  fontSize: "14px",
  width: "58%"
};

const genreStyle = {
  display: "flex",
  marginBottom: "10px",
  justifyContent: "space-between"
};

class RSScategorylist extends Component {
  constructor(props) {
    super(props);
    this.state = {
      categorylist: [
        {
          id: "cat1",
          name: "Category 1",
          value: 1
        },
        {
          id: "cat2",
          name: "Category 2",
          value: 2
        },
        {
          id: "cat3",
          name: "Category 3",
          value: 3
        },
        {
          id: "cat4",
          name: "Category 4",
          value: 4
        },
        {
          id: "cat5",
          name: "Category 5",
          value: 5
        },
        {
          id: "cat6",
          name: "Category 6",
          value: 6
        },
        {
          id: "cat7",
          name: "Category 7",
          value: 7
        },
        {
          id: "cat8",
          name: "Category 8",
          value: 8
        },
        {
          id: "cat9",
          name: "Category 9",
          value: 9
        },
        {
          id: "cat10",
          name: "Category 10",
          value: 10
        }
      ],
      checkedListAll: [],
      ItemsChecked: false,
      showGenre: false,
      isModalOpen: false
    };
  }

  handleGenreToggle = () => {
    this.setState({
      showGenre: !this.state.showGenre
    });
  };

  selectedItems = e => {
    const { value, checked } = e.target;
    let { checkedListAll } = this.state;

    if (checked) {
      checkedListAll = [...checkedListAll, value];
    } else {
      checkedListAll = checkedListAll.filter(item => item !== value);
      if (this.state.ItemsChecked) {
        this.setState({
          ItemsChecked: !this.state.ItemsChecked
        });
      }
    }
    this.setState({ checkedListAll });
  };

  selectItem = () => {
    const { ItemsChecked, categorylist } = this.state;
    const collection = [];

    if (!ItemsChecked) {
      for (const cat of categorylist) {
        collection.push(cat.value);
      }
    }

    this.setState({
      checkedListAll: collection,
      ItemsChecked: !ItemsChecked
    });
  };

  handleCheckboxClick = e => {
    const { value, checked } = e.target;

    if (checked) {
      this.setState(prevState => ({
        checkedListAll: [...prevState.checkedListAll, value * 1]
      }));
    } else {
      this.setState(prevState => ({
        checkedListAll: prevState.checkedListAll.filter(item => item != value)
      }));
    }
  };

  openModal = () => {
    this.setState({ isModalOpen: true });
  };

  closeModal = () => {
    this.setState({ isModalOpen: false });
  };

  render() {
    const {
      showGenre,
      categorylist,
      checkedListAll,
      ItemsChecked,
      isModalOpen
    } = this.state;

    return (
      <Card className="ribbon-wrapper catlist-custom">
        <CardBody className="custom-scrollbar card-body-custom card-body-catlist">
          <div className="ribbon ribbon-clip ribbon-secondary">
            File Category
          </div>
          <Col>
            <FormGroup style={formGroupStyle}>
              {showGenre && (
                <div style={genreStyle}>
                  <Input
                    type="select"
                    name="genreSel"
                    id="genreSel"
                    style={selectStyle}
                    className="catlist-select"
                  >
                    <option>1</option>
                    <option>2</option>
                    <option>3</option>
                    <option>4</option>
                    <option>5</option>
                  </Input>
                  <Button
                    outline
                    size="sm"
                    color="info"
                    className="catlist-btn"
                    onClick={this.openModal}
                  >
                    Create
                  </Button>{" "}
                  <Button
                    outline
                    size="sm"
                    color="info"
                    className="catlist-btn"
                  >
                    Edit
                  </Button>{" "}
                  <Button
                    outline
                    size="sm"
                    color="info"
                    className="catlist-btn"
                  >
                    Delete
                  </Button>
                  <GenreModal isOpen={isModalOpen} close={this.closeModal} />
                </div>
              )}
              {categorylist.map(cat => {
                return (
                  <Checkbox
                    item={cat}
                    key={cat.value}
                    selectedItems={this.selectedItems.bind(this)}
                    ItemsChecked={ItemsChecked}
                    isChecked={checkedListAll.includes(cat.value)}
                    handleCheckboxClick={this.handleCheckboxClick}
                    labelClass="catlist-label"
                  />
                );
              })}
            </FormGroup>
          </Col>
          <div style={buttonPosition}>
            <ButtonToggle
              outline
              size="sm"
              color="info"
              className={
                "catlist-btn catlist-btn-toggle" +
                (ItemsChecked ? " active" : "")
              }
              onClick={this.selectItem.bind(this)}
            >
              All
            </ButtonToggle>{" "}
            <ButtonToggle
              outline
              size="sm"
              color="info"
              className={
                "catlist-btn catlist-btn-toggle" + (showGenre ? " active" : "")
              }
              onClick={this.handleGenreToggle}
            >
              Select Genre
            </ButtonToggle>
          </div>
        </CardBody>
      </Card>
    );
  }
}

class GenreModal extends Component {
  render() {
    const { isOpen, close } = this.props;
    return (
      <>
        {isOpen ? (
          <ReactTransitionGroup
            transitionName={"Custom-modal-anim"}
            transitionEnterTimeout={200}
            transitionLeaveTimeout={200}
          >
            <div className="Custom-modal-overlay" onClick={close} />
            <div className="Custom-modal">
              <p className="title">Create Genre</p>
              <div className="content">
                <FormGroup style={{ marginTop: "1rem" }}>
                  <Input
                    type="text"
                    name="genName"
                    placeholder="Enter Genre Name"
                    className="catlist-input"
                  />
                </FormGroup>
              </div>
              <div className="button-wrap">
                <button className="form-type left-btn" onClick={close}>
                  Create
                </button>
                <button className="form-type right-btn" onClick={close}>
                  Cancel
                </button>
              </div>
            </div>
          </ReactTransitionGroup>
        ) : (
          <ReactTransitionGroup
            transitionName={"Custom-modal-anim"}
            transitionEnterTimeout={200}
            transitionLeaveTimeout={200}
          />
        )}
      </>
    );
  }
}

export default RSScategorylist;
