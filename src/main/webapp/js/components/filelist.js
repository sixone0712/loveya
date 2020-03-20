import React from "react";
import {
  Card,
  CardBody,
  Table,
  ButtonToggle,
  Pagination,
  PaginationItem,
  PaginationLink,
  Button
} from "reactstrap";

const tableStyle = {
  boxShadow: "0 2px 5px 0 rgba(0,0,0,.16), 0 2px 10px 0 rgba(0,0,0,.12)",
  marginTop: ".5rem",
  marginBottom: ".5rem"
};

const cardStyle = {
  margin: "10px 0px",
  boxShadow: "0 1px 11px 0 rgba(0,0,0,.1)"
};

const divStyle = {
  paddingLeft: "10px",
  paddingRight: "10px"
};

const theadSelect = {
  width: "5%",
  textAlign: "center"
};

const checkStyle = {
  marginLeft: "7px"
};

const paginationStyle = {
  marginLeft: "auto",
  marginRight: "auto",
  fontSize: "14px"
};

const buttonPosition = {
  position: "absolute",
  top: "17px",
  right: "20px"
};

export default function RSSfilelist() {
  return (
    <div style={divStyle}>
      <Card className="ribbon-wrapper" style={cardStyle}>
        <CardBody className="card-body-filelist">
          <div className="ribbon ribbon-clip ribbon-info">File</div>
          <Table style={tableStyle}>
            <thead>
              <tr>
                <th style={theadSelect}>
                  <div>
                    <ButtonToggle
                      outline
                      size="sm"
                      color="info"
                      className="filelist-btn filelist-btn-toggle"
                    >
                      All
                    </ButtonToggle>
                  </div>
                </th>
                <th>Machine</th>
                <th>Category</th>
                <th>File Path</th>
                <th>File Name</th>
                <th>Date</th>
                <th>Size</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td>
                  <div
                    className="custom-control custom-checkbox"
                    style={checkStyle}
                  >
                    <input
                      type="checkbox"
                      className="custom-control-input"
                      id="file1"
                    />
                    <label
                      className="custom-control-label filelist-label"
                      htmlFor="file1"
                    />
                  </div>
                </td>
                <td>Machine 1</td>
                <td>Category 1</td>
                <td>Main / Sub1 / Sub2</td>
                <td>errorhistory.log</td>
                <td>2020/02/21 14:43</td>
                <td>7KB</td>
              </tr>
              <tr>
                <td>
                  <div
                    className="custom-control custom-checkbox"
                    style={checkStyle}
                  >
                    <input
                      type="checkbox"
                      className="custom-control-input"
                      id="file2"
                    />
                    <label
                      className="custom-control-label filelist-label"
                      htmlFor="file2"
                    />
                  </div>
                </td>
                <td>Machine 2</td>
                <td>Category 2</td>
                <td>Service / Main</td>
                <td>servicehistory.log</td>
                <td>2020/02/18 14:43</td>
                <td>19KB</td>
              </tr>
              <tr>
                <td>
                  <div
                    className="custom-control custom-checkbox"
                    style={checkStyle}
                  >
                    <input
                      type="checkbox"
                      className="custom-control-input"
                      id="file3"
                    />
                    <label
                      className="custom-control-label filelist-label"
                      htmlFor="file3"
                    />
                  </div>
                </td>
                <td>Machine 1</td>
                <td>Category 1</td>
                <td>Main / Sub1 / Sub2</td>
                <td>errorhistory.log</td>
                <td>2020/02/21 14:43</td>
                <td>7KB</td>
              </tr>
              <tr>
                <td>
                  <div
                    className="custom-control custom-checkbox"
                    style={checkStyle}
                  >
                    <input
                      type="checkbox"
                      className="custom-control-input"
                      id="file4"
                    />
                    <label
                      className="custom-control-label filelist-label"
                      htmlFor="file4"
                    />
                  </div>
                </td>
                <td>Machine 1</td>
                <td>Category 1</td>
                <td>Main / Sub1 / Sub2</td>
                <td>errorhistory.log</td>
                <td>2020/02/21 14:43</td>
                <td>7KB</td>
              </tr>
              <tr>
                <td>
                  <div
                    className="custom-control custom-checkbox"
                    style={checkStyle}
                  >
                    <input
                      type="checkbox"
                      className="custom-control-input"
                      id="file5"
                    />
                    <label
                      className="custom-control-label filelist-label"
                      htmlFor="file5"
                    />
                  </div>
                </td>
                <td>Machine 1</td>
                <td>Category 1</td>
                <td>Main / Sub1 / Sub2</td>
                <td>errorhistory.log</td>
                <td>2020/02/21 14:43</td>
                <td>7KB</td>
              </tr>
              <tr>
                <td>
                  <div
                    className="custom-control custom-checkbox"
                    style={checkStyle}
                  >
                    <input
                      type="checkbox"
                      className="custom-control-input"
                      id="file6"
                    />
                    <label
                      className="custom-control-label filelist-label"
                      htmlFor="file6"
                    />
                  </div>
                </td>
                <td>Machine 1</td>
                <td>Category 1</td>
                <td>Main / Sub1 / Sub2</td>
                <td>errorhistory.log</td>
                <td>2020/02/21 14:43</td>
                <td>7KB</td>
              </tr>
              <tr>
                <td>
                  <div
                    className="custom-control custom-checkbox"
                    style={checkStyle}
                  >
                    <input
                      type="checkbox"
                      className="custom-control-input"
                      id="file7"
                    />
                    <label
                      className="custom-control-label filelist-label"
                      htmlFor="file7"
                    />
                  </div>
                </td>
                <td>Machine 1</td>
                <td>Category 1</td>
                <td>Main / Sub1 / Sub2</td>
                <td>errorhistory.log</td>
                <td>2020/02/21 14:43</td>
                <td>7KB</td>
              </tr>
              <tr>
                <td>
                  <div
                    className="custom-control custom-checkbox"
                    style={checkStyle}
                  >
                    <input
                      type="checkbox"
                      className="custom-control-input"
                      id="file8"
                    />
                    <label
                      className="custom-control-label filelist-label"
                      htmlFor="file8"
                    />
                  </div>
                </td>
                <td>Machine 1</td>
                <td>Category 1</td>
                <td>Main / Sub1 / Sub2</td>
                <td>errorhistory.log</td>
                <td>2020/02/21 14:43</td>
                <td>7KB</td>
              </tr>
              <tr>
                <td>
                  <div
                    className="custom-control custom-checkbox"
                    style={checkStyle}
                  >
                    <input
                      type="checkbox"
                      className="custom-control-input"
                      id="file9"
                    />
                    <label
                      className="custom-control-label filelist-label"
                      htmlFor="file9"
                    />
                  </div>
                </td>
                <td>Machine 1</td>
                <td>Category 1</td>
                <td>Main / Sub1 / Sub2</td>
                <td>errorhistory.log</td>
                <td>2020/02/21 14:43</td>
                <td>7KB</td>
              </tr>
              <tr>
                <td>
                  <div
                    className="custom-control custom-checkbox"
                    style={checkStyle}
                  >
                    <input
                      type="checkbox"
                      className="custom-control-input"
                      id="file10"
                    />
                    <label
                      className="custom-control-label filelist-label"
                      htmlFor="file10"
                    />
                  </div>
                </td>
                <td>Machine 1</td>
                <td>Category 1</td>
                <td>Main / Sub1 / Sub2</td>
                <td>errorhistory.log</td>
                <td>2020/02/21 14:43</td>
                <td>7KB</td>
              </tr>
            </tbody>
          </Table>
        </CardBody>
        <div style={paginationStyle}>
          <Pagination aria-label="filelist">
            <PaginationItem>
              <PaginationLink className="filelist-page-link" first href="#" />
            </PaginationItem>
            <PaginationItem>
              <PaginationLink
                className="filelist-page-link"
                previous
                href="#"
              />
            </PaginationItem>
            <PaginationItem active>
              <PaginationLink className="filelist-page-link" href="#">
                1
              </PaginationLink>
            </PaginationItem>
            <PaginationItem>
              <PaginationLink className="filelist-page-link" href="#">
                2
              </PaginationLink>
            </PaginationItem>
            <PaginationItem>
              <PaginationLink className="filelist-page-link" href="#">
                3
              </PaginationLink>
            </PaginationItem>
            <PaginationItem>
              <PaginationLink className="filelist-page-link" href="#">
                4
              </PaginationLink>
            </PaginationItem>
            <PaginationItem>
              <PaginationLink className="filelist-page-link" href="#">
                5
              </PaginationLink>
            </PaginationItem>
            <PaginationItem>
              <PaginationLink className="filelist-page-link" href="#">
                6
              </PaginationLink>
            </PaginationItem>
            <PaginationItem>
              <PaginationLink className="filelist-page-link" href="#">
                7
              </PaginationLink>
            </PaginationItem>
            <PaginationItem>
              <PaginationLink className="filelist-page-link" href="#">
                8
              </PaginationLink>
            </PaginationItem>
            <PaginationItem>
              <PaginationLink className="filelist-page-link" href="#">
                9
              </PaginationLink>
            </PaginationItem>
            <PaginationItem>
              <PaginationLink className="filelist-page-link" href="#">
                10
              </PaginationLink>
            </PaginationItem>
            <PaginationItem>
              <PaginationLink className="filelist-page-link" next href="#" />
            </PaginationItem>
            <PaginationItem>
              <PaginationLink className="filelist-page-link" last href="#" />
            </PaginationItem>
          </Pagination>
        </div>
        <div style={buttonPosition}>
          <Button outline size="sm" color="info" className="filelist-btn">
            Download
          </Button>
        </div>
      </Card>
    </div>
  );
}
