import React, { useEffect, useState } from 'react';
import { Button, Row, Col, Select, Card, Tabs, Table, Tag, Pagination } from 'antd';
import { LeftOutlined, QuestionCircleOutlined, SortAscendingOutlined } from '@ant-design/icons';
import { NavLink, useParams, useHistory } from 'react-router-dom';
import { bindActionCreators } from 'redux'
import { connect } from 'react-redux';
import * as employeeActions from '../../actions/employee';
import PropTypes from 'prop-types';
import moment from 'moment';
import { formatDate } from "../../utils/DateFormat"
import { formatMonney } from "../../utils/MonneyFormat"
import { formatPlate } from "../../utils/PlatesNumberFormat"
import Search from 'antd/lib/input/Search';


const EmployeeInfo = (props) => {
    const history = useHistory();
    const columns = [
        {
            title: 'Mã Phiếu', dataIndex: 'code', key: 'code', sorter: true, render: (text, data) => {
                return (<div className="link"><NavLink to={`/admin/maintenanceCards/${data.id}`}>{text.toUpperCase()}</NavLink></div>)
            },
        },
        { title: 'Khách hàng', dataIndex: 'customer', key: 'customer', render: (text, data) => text.name, sorter: true },
        { title: 'Biển số', dataIndex: 'platesNumber', key: 'platesNumber', render: (text, data) => formatPlate(text) },
        { title: 'Tổng tiền', dataIndex: 'price', key: 'price', sorter: true, render: (text, data) => formatMonney(text) },
        { title: 'Màu xe', dataIndex: 'color', key: 'color', sorter: true },
        { title: 'NV điều phối', dataIndex: 'coordinator', key: 'coordinator', render: (text, data) => text.fullName, sorter: true },
        {
            title: 'NV sửa chữa ', dataIndex: 'repairman', key: 'repairman', sorter: true, render: (text, data) => {
                if (data.repairman != null && data.repairman != undefined) {
                    return text.fullName
                }
            }
        },
        {
            title: 'Trạng thái', dataIndex: 'payStatus', key: 'payStatus',
            filters: [
                { text: 'Chưa thanh toán', value: '0' },
                { text: 'Đã thanh toán', value: '1' },
            ],
            render: (payStatus) => {
                if (payStatus === 0) {
                    return <Tag color='warning'>Chưa thanh toán</Tag>
                }
                else {
                    return <Tag color='success'>Đã thanh toán</Tag>
                }
            }
        },
        {
            title: 'Trạng thái công việc',
            dataIndex: 'workStatus',
            key: 'workStatus',

            filters: [
                { text: 'Đang chờ', value: '0' },
                { text: 'Đang sửa', value: '1' },
                { text: 'Hoàn thành', value: '2' },
            ],
            render: (status) => {
                if (status === 0) {
                    return <Tag color='warning'>Đang chờ</Tag>
                }
                else if (status === 1) {
                    return <Tag color='processing'>Đang sửa</Tag>
                }
                else {
                    return <Tag color='success'>Hoàn thành</Tag>
                }
            }
        },
    ];

    const id = useParams().id;

    const { Option } = Select;
    const { employeeActionsCreator } = props;
    const { actGetEmployee } = employeeActionsCreator;
    const { actDeleteEmployee } = employeeActionsCreator;
    const { actgetMaintenanceCardByUserId } = employeeActionsCreator;
    const { TabPane } = Tabs;
    const { userItem } = props;
    const [user, setUser] = useState(props.userItem);
    const { maintenanceCard, totalPage, totalElement, currentPage } = props;
    const [current, setCurrent] = useState(1);
    const [pageNumber, setPageNumber] = useState(1);
    const [size, setSize] = useState(5);
    const [search, setSearch] = useState('');
    const [nameField, setNameField] = useState();
    const [order, setOrder] = useState();
    const [filter, setFilter] = useState({});

    useEffect(() => {
        console.log('alo');
        actGetEmployee(id, 5, '', '', '');
        actgetMaintenanceCardByUserId(id, pageNumber, size, '', '', search, "", "");
    }, [actGetEmployee, actgetMaintenanceCardByUserId, pageNumber, size, current, search])
    useEffect(() => {
        actGetEmployee(props.match.params.id, 5, '', '', '');
        actgetMaintenanceCardByUserId(props.match.params.id, pageNumber, size, '', '', search, "", "");
        // actgetMaintenanceCardByUserId(props.match.params.id,pageNumber,size,'','','');
    }, [props.match.params.id, search])
    const handleChangeOption = (value) => {
        console.log(value);
        if (value === 'update') {
            history.push(`../../admin/employees/update/${id}`);
        }
        if (value === 'delete') {
            const arrayId = [id];
            actDeleteEmployee(arrayId);
            history.push('../../admin/employees')
        }
    }
    const renderTitleCard = () => {
        return (
            <>
                <Row>
                    <Col span={8}>Thông tin cá nhân</Col>
                    <Col span={8}>Vai trò: {(userItem.role == 1 ? 'Nhân viên điều phối' : userItem.role === 2 ? 'Nhân viên sửa chữa' : userItem.role === 3 ? 'Quản lý' : '')}</Col>
                    <Col span={8}>
                        <Select value="Chọn chức năng " style={{ float: 'right', width: 250 }} onChange={handleChangeOption} >
                            <Option value="update">Cập nhật thông tin nhân viên</Option>
                            <Option value="delete">Xóa nhân viên</Option>
                        </Select></Col>
                </Row>
            </>
        )
    }
    const handleTableChange = (pagination, filters, sorter) => {
        setFilter(filters);
        let descending = sorter.order === 'ascend' ? false : true;


        if (sorter && sorter.field !== undefined && (filter.workStatus == null || filter.payStatus == null)) {
            console.log('sortter');
            actgetMaintenanceCardByUserId(id, pageNumber, size, sorter.field, descending, "", "", "");
        } else {
            if (filters.payStatus == null) {
                console.log('paySatus Null');
                filters.payStatus = [];

            }
            if (filters.workStatus == null) {
                console.log('workStatus Null');
                filters.workStatus = [];
            }
            actgetMaintenanceCardByUserId(id, pageNumber, size, "", descending, "", filters.payStatus, filters.workStatus);

        }
    };
    const mapdata = () => {

        let data = [];
        if (maintenanceCard != undefined) {
            data = maintenanceCard.map((val) => {
                return {
                    key: val.id,
                    price: val.price,
                    ...val
                }
            })
        }
        return data;
    }
    const onChange = (page) => {
        console.log("Page: ", page);
        setCurrent(page)
        setPageNumber(page)
        console.log(current, page);
    }
    const changePageSize = (current, size) => {
        setPageNumber(current)
        setSize(size)
    }
    const handleChangeSearch = (e) => {
        setSearch(e.target.value);
        console.log(e.target.value);
        actgetMaintenanceCardByUserId(id, pageNumber, size, '', '', e.target.value, "", "");

    }
    const renderBodyCard = (props) => {

        return (
            <>
                <Row>
                    <Col span={8}>
                        <Row style={{ marginBottom: 6, marginTop: 8 }}>
                            <Col span={10}>Mã nhân viên </Col>
                            <Col span={14}>: {props.code === undefined ? '' : props.code.toUpperCase()}</Col>
                        </Row>
                        <Row style={{ marginBottom: 6 }} >
                            <Col span={10}>Tên nhân viên </Col>
                            <Col span={14}>: {props.fullName}</Col>
                        </Row>

                    </Col>
                    <Col span={8}>
                        <Row style={{ marginBottom: 6, marginTop: 8 }}>
                            <Col span={10}>Số điện thoại :</Col>
                            <Col span={14}>: {props.phoneNumber}</Col>
                        </Row>

                        <Row style={{ marginBottom: 6 }} >
                            <Col span={10}>Email </Col>
                            <Col span={14}>: {props.email}</Col>
                        </Row>
                    </Col>

                    <Col span={8}>
                        <Row style={{ marginBottom: 6, marginTop: 8 }}>
                            <Col span={10}>Ngày tạo </Col>
                            <Col span={14}>: {formatDate(props.createdDate)}</Col>
                        </Row>
                        <Row style={{ marginBottom: 6 }} >
                            <Col span={10}>Ngày sửa </Col>
                            <Col span={14}>: {formatDate(props.modifiedDate)}</Col>
                        </Row>
                    </Col>
                </Row>
                <Col span={8}>
                    <Row style={{ marginBottom: 6, marginTop: 8 }}>
                        <Col span={10}>Địa chỉ </Col>
                        <Col span={14}  >  : {props.address != null ? props.address : "--"}</Col>
                    </Row>
                </Col>
            </>
        )
    }


    return (
        <>
            <div style={{ width: '98%', marginRight: '1%', marginLeft: '1%' }}>
                <div style={{ marginBottom: 16 }}>
                    <p><NavLink to='/admin/employees'><LeftOutlined /> Danh sách nhân viên</NavLink></p>
                    <span style={{ fontWeight: 'bold', fontSize: 30 }}>
                        {userItem.fullName}
                    </span>

                </div>

                <div style={{ marginBottom: 16, width: '100%' }}>
                    <Card title={renderTitleCard()} bordered={false} style={{ width: '100%', borderRadius: 3 }}>
                        {renderBodyCard(userItem)}
                    </Card>
                </div>
                <div style={{ marginBottom: 16, width: '100%', marginTop: 20 }}>
                    <Card bordered={true} style={{ width: '100%', borderRadius: 3 }}>

                        <Tabs defaultActiveKey="1">
                            <TabPane tab={`Thông tin phiếu ${userItem.role === 1 ? "đã phiếu phối" : "đã sửa chữa"}`} key="1">
                                <div style={{ width: '98%', marginRight: '1%', marginLeft: '1%' }}>
    

                                        <div style={{ float: 'right' }}>
                                            <Search
                                                placeholder="Tìm kiếm nhân viên theo Tên, Mã, Email, hoặc SĐT"

                                                style={{ width: 400, marginTop: 20, marginRight: 10 }}
                                                onChange={handleChangeSearch}
                                                value={search}
                                                allowClear={true}
                                            />



                                        </div>
                                    </div>
                                    <Table
                                        columns={columns}
                                        dataSource={mapdata()}
                                        onChange={handleTableChange}
                                        pagination={false}
                                        locale={{
                                            filterConfirm: 'Tìm kiếm',
                                            filterReset: 'Đặt lại',
                                            emptyText: "Không có phiếu sửa chữa nào",
                                            triggerDesc: 'Sắp xếp giảm dần',
                                            triggerAsc: 'Sắp xếp giảm dần',
                                            cancelSort: 'Hủy sắp xếp',
                                        }}
                                        onRow={(r) => ({
                                            onClick: () => {

                                                history.push(`/admin/maintenanceCards/${r.id}`)
                                            },

                                        })}
                                    />
                                    <div style={{ float: 'right', marginTop: 10 }}>
                                        <Pagination current={current} total={totalElement} defaultPageSize={size} onChange={onChange} showSizeChanger={true} pageSizeOptions={[5, 10, 20, 50]} onShowSizeChange={changePageSize}
                                            locale={{ items_per_page: '/ Trang' }}
                                        />
                                    </div>
               

                            </TabPane>
                        </Tabs>


                    </Card>
                </div>


            </div>
        </>
    );
}

EmployeeInfo.propTypes = {
    createEmployee: PropTypes.shape({
        employeeActionsCreator: PropTypes.func,
    })
}

const mapStateToProps = state => {
    return {
        userItem: state.employeeReducer.userItem,
        maintenanceCard: state.employeeReducer.maintenanceCard,
        totalElement: state.employeeReducer.totalElement,
        totalPage: state.employeeReducer.totalPage
    }
};

const mapDispatchToProps = dispatch => {
    return {
        employeeActionsCreator: bindActionCreators(employeeActions, dispatch)
    }
};

export default (connect(mapStateToProps, mapDispatchToProps)(EmployeeInfo));

