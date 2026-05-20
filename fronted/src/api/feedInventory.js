import request from './index'

export function listFeedInventory(params) {
  return request({
    url: '/feed-inventory',
    method: 'get',
    params
  })
}

export function listAllFeedInventory() {
  return request({
    url: '/feed-inventory/all',
    method: 'get'
  })
}

export function getFeedInventory(id) {
  return request({
    url: `/feed-inventory/${id}`,
    method: 'get'
  })
}

export function saveFeedInventory(data) {
  return request({
    url: '/feed-inventory',
    method: 'post',
    data
  })
}

export function updateFeedInventory(id, data) {
  return request({
    url: `/feed-inventory/${id}`,
    method: 'put',
    data
  })
}

export function deleteFeedInventory(id) {
  return request({
    url: `/feed-inventory/${id}`,
    method: 'delete'
  })
}